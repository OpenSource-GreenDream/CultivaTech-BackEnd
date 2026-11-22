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
@Entity @Table(name = "products")
public class ProductEntity extends AuditableAbstractPersistenceEntity {
    @Column(name="inventory_id", nullable=false, unique=true) private Long inventoryId;
    @Column(name="field_id") private Long fieldId;
    @Column(nullable=false, length=255) private String name;
    @Column(length=1000) private String description;
    @Column(nullable=false, precision=18, scale=2) private BigDecimal price;
    @Column(nullable=false, length=50) private String type;
    @Column(name="image_url", length=500) private String imageUrl;
}
