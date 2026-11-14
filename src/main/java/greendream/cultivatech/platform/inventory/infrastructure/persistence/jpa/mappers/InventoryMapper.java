package greendream.cultivatech.platform.inventory.infrastructure.persistence.jpa.mappers;

import greendream.cultivatech.platform.inventory.domain.model.aggregates.Inventory;
import greendream.cultivatech.platform.inventory.domain.model.valueobjects.FieldId;
import greendream.cultivatech.platform.inventory.domain.model.valueobjects.InventoryId;
import greendream.cultivatech.platform.inventory.domain.model.valueobjects.ProfileId;
import greendream.cultivatech.platform.inventory.domain.model.valueobjects.StockQuantity;
import greendream.cultivatech.platform.inventory.infrastructure.persistence.jpa.entities.InventoryPersistenceEntity;
import org.springframework.stereotype.Component;

/**
 * Maps between the Inventory aggregate and its persistence representation.
 */
@Component
public class InventoryMapper {

    public InventoryPersistenceEntity toNewEntity(Inventory inventory) {
        return new InventoryPersistenceEntity(
                inventory.profileId().value(),
                inventory.fieldId().value(),
                inventory.stockQuantity().value()
        );
    }

    public void updateEntity(InventoryPersistenceEntity entity, Inventory inventory) {
        entity.setFieldId(inventory.fieldId().value());
        entity.setStockQuantity(inventory.stockQuantity().value());
    }

    public Inventory toDomain(InventoryPersistenceEntity entity) {
        return Inventory.reconstitute(
                new InventoryId(entity.getId()),
                new ProfileId(entity.getProfileId()),
                new FieldId(entity.getFieldId()),
                new StockQuantity(entity.getStockQuantity())
        );
    }
}
