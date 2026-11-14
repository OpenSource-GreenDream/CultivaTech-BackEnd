package greendream.cultivatech.platform.inventory.domain.repositories;

import greendream.cultivatech.platform.inventory.domain.model.aggregates.Inventory;
import greendream.cultivatech.platform.inventory.domain.model.valueobjects.FieldId;
import greendream.cultivatech.platform.inventory.domain.model.valueobjects.InventoryId;
import greendream.cultivatech.platform.inventory.domain.model.valueobjects.ProfileId;
import greendream.cultivatech.platform.shared.domain.repositories.ICrudService;

import java.util.List;

/**
 * Persistence contract for the inventory aggregate.
 */
public interface InventoryRepository extends ICrudService<Inventory, InventoryId> {

    List<Inventory> findByProfileId(ProfileId profileId);

    List<Inventory> findByFieldId(FieldId fieldId);
}
