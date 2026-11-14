package greendream.cultivatech.platform.inventory.infrastructure.persistence.jpa.entities;

import greendream.cultivatech.platform.shared.infrastructure.persistence.jpa.AuditableAbstractPersistenceEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * Persistence representation of the inventories table.
 */
@Setter
@Getter
@Entity
@Table(
        name = "inventories",
        indexes = {
                @Index(name = "idx_inventories_profile_id", columnList = "profile_id"),
                @Index(name = "idx_inventories_field_id", columnList = "field_id")
        }
)
public class InventoryPersistenceEntity extends AuditableAbstractPersistenceEntity {

    @Column(name = "profile_id", nullable = false)
    private Long profileId;

    @Column(name = "field_id", nullable = false)
    private Long fieldId;

    @Column(name = "stock_quantity", nullable = false)
    private int stockQuantity;

    protected InventoryPersistenceEntity() {
    }

    public InventoryPersistenceEntity(Long profileId,
                                      Long fieldId,
                                      int stockQuantity) {
        this.profileId = profileId;
        this.fieldId = fieldId;
        this.stockQuantity = stockQuantity;
    }
}
