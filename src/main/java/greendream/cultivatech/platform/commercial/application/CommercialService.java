package greendream.cultivatech.platform.commercial.application;

import greendream.cultivatech.platform.commercial.domain.model.aggregates.Order;
import greendream.cultivatech.platform.commercial.domain.model.aggregates.Product;
import greendream.cultivatech.platform.commercial.domain.repositories.OrderRepository;
import greendream.cultivatech.platform.commercial.domain.repositories.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;

/** Application use cases depend on Commercial domain ports, not persistence details. */
@Service
@Transactional
public class CommercialService {
    private final ProductRepository products;
    private final OrderRepository orders;

    public CommercialService(ProductRepository products, OrderRepository orders) {
        this.products = products;
        this.orders = orders;
    }

    @Transactional(readOnly = true)
    public List<Product> getProducts() { return products.findAll(); }

    @Transactional(readOnly = true)
    public Product getProduct(Long id) { return products.findById(id).orElseThrow(() -> new IllegalArgumentException("Product not found")); }

    public Product createProduct(Long inventoryId, Long fieldId, String name, String description, BigDecimal price, String imageUrl) {
        if (products.findByInventoryId(inventoryId).isPresent()) throw new IllegalArgumentException("A product already exists for this inventory");
        return products.save(new Product(null, inventoryId, fieldId, name, description, price, imageUrl));
    }

    public Product updateProduct(Long id, Long inventoryId, Long fieldId, String name, String description, BigDecimal price, String imageUrl) {
        getProduct(id);
        products.findByInventoryId(inventoryId).filter(existing -> !existing.id().equals(id))
                .ifPresent(existing -> { throw new IllegalArgumentException("A product already exists for this inventory"); });
        return products.save(new Product(id, inventoryId, fieldId, name, description, price, imageUrl));
    }

    public void deleteProduct(Long id) { getProduct(id); products.deleteById(id); }

    @Transactional(readOnly = true)
    public List<Order> getOrders(Long profileId) {
        if (profileId == null) return orders.findAll();
        if (profileId <= 0) throw new IllegalArgumentException("Profile id must be positive");
        return orders.findAllByProfileId(profileId);
    }

    @Transactional(readOnly = true)
    public Order getOrder(Long id) { return orders.findById(id).orElseThrow(() -> new IllegalArgumentException("Order not found")); }

    public Order createOrder(Long profileId, Long productId, int quantity, int paymentMethod, boolean subscription) {
        Product product = getProduct(productId);
        BigDecimal total = product.price().multiply(BigDecimal.valueOf(quantity));
        return orders.save(new Order(null, profileId, productId, product.name(), quantity, total, 0, paymentMethod, subscription));
    }

    public Order updateOrderStatus(Long id, int status) {
        Order order = getOrder(id);
        return orders.save(new Order(order.id(), order.profileId(), order.productId(), order.productName(),
                order.quantity(), order.totalAmount(), status, order.paymentMethod(), order.subscription()));
    }
}
