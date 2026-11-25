package greendream.cultivatech.platform.commercial.infrastructure.persistence.jpa.adapters;

import greendream.cultivatech.platform.commercial.domain.model.aggregates.Order;
import greendream.cultivatech.platform.commercial.domain.repositories.OrderRepository;
import greendream.cultivatech.platform.commercial.infrastructure.persistence.jpa.entities.OrderEntity;
import greendream.cultivatech.platform.commercial.infrastructure.persistence.jpa.repositories.OrderJpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/** JPA adapter translating order rows to the Commercial domain model. */
@Repository
public class OrderRepositoryAdapter implements OrderRepository {
    private final OrderJpaRepository jpa;
    public OrderRepositoryAdapter(OrderJpaRepository jpa) { this.jpa = jpa; }

    @Override public List<Order> findAll() { return jpa.findAll().stream().map(this::toDomain).toList(); }
    @Override public List<Order> findAllByProfileId(Long id) { return jpa.findAllByProfileIdOrderByCreatedAtDesc(id).stream().map(this::toDomain).toList(); }
    @Override public Optional<Order> findById(Long id) { return jpa.findById(id).map(this::toDomain); }
    @Override public boolean existsByProductId(Long productId) { return jpa.existsByProductId(productId); }
    @Override public Order save(Order order) {
        OrderEntity entity = order.id() == null ? new OrderEntity() : jpa.findById(order.id()).orElseGet(OrderEntity::new);
        entity.setProfileId(order.profileId()); entity.setProductId(order.productId()); entity.setProductName(order.productName());
        entity.setQuantity(order.quantity()); entity.setTotalAmount(order.totalAmount()); entity.setStatus(order.status());
        entity.setPaymentMethod(order.paymentMethod()); entity.setSubscription(order.subscription());
        return toDomain(jpa.save(entity));
    }

    private Order toDomain(OrderEntity entity) {
        return new Order(entity.getId(), entity.getProfileId(), entity.getProductId(), entity.getProductName(),
                entity.getQuantity(), entity.getTotalAmount(), entity.getStatus(), entity.getPaymentMethod(),
                Boolean.TRUE.equals(entity.getSubscription()));
    }
}
