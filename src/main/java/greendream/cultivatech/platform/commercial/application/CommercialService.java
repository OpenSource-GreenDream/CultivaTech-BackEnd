package greendream.cultivatech.platform.commercial.application;

import greendream.cultivatech.platform.commercial.domain.model.aggregates.Order;
import greendream.cultivatech.platform.commercial.domain.model.aggregates.Product;
import greendream.cultivatech.platform.commercial.infrastructure.persistence.jpa.entities.OrderEntity;
import greendream.cultivatech.platform.commercial.infrastructure.persistence.jpa.entities.ProductEntity;
import greendream.cultivatech.platform.commercial.infrastructure.persistence.jpa.repositories.OrderJpaRepository;
import greendream.cultivatech.platform.commercial.infrastructure.persistence.jpa.repositories.ProductJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;

/** Use cases for the product catalog and customer orders. */
@Service
@Transactional
public class CommercialService {
    private final ProductJpaRepository products;
    private final OrderJpaRepository orders;

    public CommercialService(ProductJpaRepository products, OrderJpaRepository orders) {
        this.products = products;
        this.orders = orders;
    }

    @Transactional(readOnly = true)
    public List<ProductEntity> getProducts() { return products.findAll(); }

    @Transactional(readOnly = true)
    public ProductEntity getProduct(Long id) { return products.findById(id).orElseThrow(() -> new IllegalArgumentException("Product not found")); }

    public ProductEntity createProduct(Long inventoryId, Long fieldId, String name, String description, BigDecimal price, String imageUrl) {
        if (products.findByInventoryId(inventoryId).isPresent()) throw new IllegalArgumentException("A product already exists for this inventory");
        Product p = new Product(null, inventoryId, fieldId, name, description, price, imageUrl);
        return products.save(productEntity(p));
    }

    public ProductEntity updateProduct(Long id, Long inventoryId, Long fieldId, String name, String description, BigDecimal price, String imageUrl) {
        ProductEntity entity = getProduct(id);
        products.findByInventoryId(inventoryId).filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> { throw new IllegalArgumentException("A product already exists for this inventory"); });
        Product p = new Product(id, inventoryId, fieldId, name, description, price, imageUrl);
        entity.setInventoryId(p.inventoryId()); entity.setFieldId(p.fieldId()); entity.setName(p.name());
        entity.setDescription(p.description()); entity.setPrice(p.price()); entity.setImageUrl(p.imageUrl());
        return products.save(entity);
    }

    public void deleteProduct(Long id) { products.delete(getProduct(id)); }

    @Transactional(readOnly = true)
    public List<OrderEntity> getOrders(Long profileId) {
        if (profileId == null) return orders.findAll();
        if (profileId <= 0) throw new IllegalArgumentException("Profile id must be positive");
        return orders.findAllByProfileIdOrderByCreatedAtDesc(profileId);
    }

    @Transactional(readOnly = true)
    public OrderEntity getOrder(Long id) { return orders.findById(id).orElseThrow(() -> new IllegalArgumentException("Order not found")); }

    public OrderEntity createOrder(Long profileId, Long productId, int quantity, int paymentMethod, boolean subscription) {
        ProductEntity product = getProduct(productId);
        BigDecimal total = product.getPrice().multiply(BigDecimal.valueOf(quantity));
        Order order = new Order(null, profileId, productId, product.getName(), quantity, total, 0, paymentMethod, subscription);
        OrderEntity entity = new OrderEntity();
        entity.setProfileId(order.profileId()); entity.setProductId(order.productId()); entity.setProductName(order.productName());
        entity.setQuantity(order.quantity()); entity.setTotalAmount(order.totalAmount()); entity.setStatus(order.status());
        entity.setPaymentMethod(order.paymentMethod()); entity.setSubscription(order.subscription());
        return orders.save(entity);
    }

    public OrderEntity updateOrderStatus(Long id, int status) {
        if (status < 0) throw new IllegalArgumentException("Status must be non-negative");
        OrderEntity entity = getOrder(id); entity.setStatus(status); return orders.save(entity);
    }

    private ProductEntity productEntity(Product p) {
        ProductEntity entity = new ProductEntity();
        entity.setInventoryId(p.inventoryId()); entity.setFieldId(p.fieldId()); entity.setName(p.name());
        entity.setDescription(p.description()); entity.setPrice(p.price()); entity.setImageUrl(p.imageUrl());
        return entity;
    }
}
