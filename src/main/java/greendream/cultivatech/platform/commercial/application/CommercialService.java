package greendream.cultivatech.platform.commercial.application;

import greendream.cultivatech.platform.commercial.domain.model.aggregates.Order;
import greendream.cultivatech.platform.commercial.domain.model.aggregates.Product;
import greendream.cultivatech.platform.commercial.domain.model.enums.OrderStatus;
import greendream.cultivatech.platform.commercial.domain.repositories.OrderRepository;
import greendream.cultivatech.platform.commercial.domain.repositories.ProductRepository;
import greendream.cultivatech.platform.inventory.domain.model.aggregates.Inventory;
import greendream.cultivatech.platform.inventory.domain.model.valueobjects.InventoryId;
import greendream.cultivatech.platform.inventory.domain.repositories.InventoryRepository;
import greendream.cultivatech.platform.profile.domain.model.aggregates.Profile;
import greendream.cultivatech.platform.profile.domain.model.valueobjects.ProfileId;
import greendream.cultivatech.platform.profile.domain.model.valueobjects.UserId;
import greendream.cultivatech.platform.profile.domain.repositories.FieldRepository;
import greendream.cultivatech.platform.profile.domain.repositories.ProfileRepository;
import greendream.cultivatech.platform.shared.domain.exceptions.ConflictException;
import greendream.cultivatech.platform.shared.domain.exceptions.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class CommercialService {
    private final ProductRepository products;
    private final OrderRepository orders;
    private final InventoryRepository inventories;
    private final ProfileRepository profiles;
    private final FieldRepository fields;

    public CommercialService(ProductRepository products,
                             OrderRepository orders,
                             InventoryRepository inventories,
                             ProfileRepository profiles,
                             FieldRepository fields) {
        this.products = products;
        this.orders = orders;
        this.inventories = inventories;
        this.profiles = profiles;
        this.fields = fields;
    }

    @Transactional(readOnly = true)
    public List<Product> getProducts() { return products.findAll(); }

    @Transactional(readOnly = true)
    public Product getProduct(Long id) { return products.findById(id).orElseThrow(() -> new NotFoundException("errors.not-found")); }

    public Product createProduct(Long inventoryId, Long fieldId, String name, String description, BigDecimal price, String type, String imageUrl) {
        if (inventoryId == null || inventoryId <= 0) {
            throw new IllegalArgumentException("error.product.inventory.required");
        }
        Inventory inventory = inventories.findById(new InventoryId(inventoryId))
                .orElseThrow(() -> new NotFoundException("errors.not-found"));
        if (products.findByInventoryId(inventoryId).isPresent()) {
            throw new ConflictException("error.product.inventory.already-associated");
        }
        if (fieldId != null) {
            if (fieldId <= 0) {
                throw new IllegalArgumentException("error.field.id.invalid");
            }
            greendream.cultivatech.platform.profile.domain.model.aggregates.Field field =
                    fields.findById(new greendream.cultivatech.platform.profile.domain.model.valueobjects.FieldId(fieldId))
                            .orElseThrow(() -> new NotFoundException("errors.not-found"));
            if (!field.profileId().value().equals(inventory.profileId().value())) {
                throw new ConflictException("error.product.field.inconsistent");
            }
        }
        return products.save(new Product(null, inventoryId, fieldId, name, description, price, type, imageUrl));
    }

    public Product updateProduct(Long id, Long inventoryId, Long fieldId, String name, String description, BigDecimal price, String type, String imageUrl) {
        Product existing = getProduct(id);
        if (inventoryId == null || inventoryId <= 0) {
            throw new IllegalArgumentException("error.product.inventory.required");
        }
        if (!inventoryId.equals(existing.inventoryId())) {
            if (orders.existsByProductId(id)) {
                throw new ConflictException("error.product.inventory.locked");
            }
            Inventory inventory = inventories.findById(new InventoryId(inventoryId))
                    .orElseThrow(() -> new NotFoundException("errors.not-found"));
            if (products.findByInventoryId(inventoryId).filter(p -> !p.id().equals(id)).isPresent()) {
                throw new ConflictException("error.product.inventory.already-associated");
            }
            if (fieldId != null) {
                greendream.cultivatech.platform.profile.domain.model.aggregates.Field field =
                        fields.findById(new greendream.cultivatech.platform.profile.domain.model.valueobjects.FieldId(fieldId))
                                .orElseThrow(() -> new NotFoundException("errors.not-found"));
                if (!field.profileId().value().equals(inventory.profileId().value())) {
                    throw new ConflictException("error.product.field.inconsistent");
                }
            }
        } else {
            Inventory inventory = inventories.findById(new InventoryId(inventoryId))
                    .orElseThrow(() -> new NotFoundException("errors.not-found"));
            if (fieldId != null) {
                greendream.cultivatech.platform.profile.domain.model.aggregates.Field field =
                        fields.findById(new greendream.cultivatech.platform.profile.domain.model.valueobjects.FieldId(fieldId))
                                .orElseThrow(() -> new NotFoundException("errors.not-found"));
                if (!field.profileId().value().equals(inventory.profileId().value())) {
                    throw new ConflictException("error.product.field.inconsistent");
                }
            }
        }
        return products.save(new Product(id, inventoryId, fieldId, name, description, price, type, imageUrl));
    }

    public void deleteProduct(Long id) {
        getProduct(id);
        if (orders.existsByProductId(id)) {
            throw new ConflictException("error.product.has-orders");
        }
        products.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<Order> getOrders(Long profileId) {
        if (profileId == null) return orders.findAll();
        if (profileId <= 0) throw new IllegalArgumentException("Profile id must be positive");
        return orders.findAllByProfileId(profileId);
    }

    @Transactional(readOnly = true)
    public Order getOrder(Long id) { return orders.findById(id).orElseThrow(() -> new NotFoundException("errors.not-found")); }

    public Order createOrder(Long profileId, Long productId, int quantity, int paymentMethod, boolean subscription) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("error.order.quantity.invalid");
        }
        if (profileId == null || profileId <= 0) {
            throw new IllegalArgumentException("error.profile.id.invalid");
        }
        profiles.findById(new ProfileId(profileId))
                .orElseThrow(() -> new NotFoundException("errors.not-found"));

        Product product = getProduct(productId);
        Inventory inventory = inventories.findById(new InventoryId(product.inventoryId()))
                .orElseThrow(() -> new NotFoundException("errors.not-found"));

        if (inventory.stockQuantity().value() < quantity) {
            throw new ConflictException("error.inventory.insufficient-stock");
        }

        BigDecimal total = product.price().multiply(BigDecimal.valueOf(quantity));
        Order order = new Order(null, profileId, productId, product.name(), quantity, total,
                OrderStatus.PENDING.code(), paymentMethod, subscription);

        inventory.decreaseStockQuantity(quantity);
        inventories.save(inventory);
        return orders.save(order);
    }

    public Order updateOrderStatus(Long id, int statusCode) {
        OrderStatus requested = OrderStatus.fromCode(statusCode);
        Order order = getOrder(id);
        OrderStatus current = OrderStatus.fromCode(order.status());

        if (!current.canTransitionTo(requested)) {
            throw new ConflictException("error.order.transition.invalid");
        }

        if (requested == OrderStatus.CANCELLED && current != OrderStatus.CANCELLED) {
            if (current == OrderStatus.PENDING || current == OrderStatus.CONFIRMED) {
                Product product = getProduct(order.productId());
                Inventory inventory = inventories.findById(new InventoryId(product.inventoryId()))
                        .orElseThrow(() -> new NotFoundException("errors.not-found"));
                inventory.increaseStockQuantity(order.quantity());
                inventories.save(inventory);
            }
        }

        return orders.save(new Order(order.id(), order.profileId(), order.productId(), order.productName(),
                order.quantity(), order.totalAmount(), requested.code(), order.paymentMethod(), order.subscription()));
    }
}