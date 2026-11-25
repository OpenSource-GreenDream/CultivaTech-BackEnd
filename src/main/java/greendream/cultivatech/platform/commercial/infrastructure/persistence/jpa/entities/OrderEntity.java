package greendream.cultivatech.platform.commercial.infrastructure.persistence.jpa.entities;

import greendream.cultivatech.platform.shared.infrastructure.persistence.jpa.AuditableAbstractPersistenceEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;

@Getter @Setter @NoArgsConstructor
@Entity @Table(name = "orders")
public class OrderEntity extends AuditableAbstractPersistenceEntity {
    @Column(name="profile_id", nullable=false) private Long profileId;
    @Column(name="product_id", nullable=false) private Long productId;
    @Column(name="product_name", nullable=false, length=255) private String productName;
    @Column(nullable=false) private Integer quantity;
    @Column(name="total_amount", nullable=false, precision=18, scale=2) private BigDecimal totalAmount;
    @Column(nullable=false) private Integer status;
    @Column(name="payment_method", nullable=false) private Integer paymentMethod;
    @Column(name="is_subscription", nullable=false) private Boolean subscription = false;
}
