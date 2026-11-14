package greendream.cultivatech.platform.inventory.infrastructure.persistence.jpa.repositories;

import greendream.cultivatech.platform.inventory.infrastructure.persistence.jpa.entities.InventoryPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Spring Data repository for the inventories table.
 */
public interface InventoryJpaRepository extends JpaRepository<InventoryPersistenceEntity, Long> {

    List<InventoryPersistenceEntity> findByProfileId(Long profileId);

    List<InventoryPersistenceEntity> findByFieldId(Long fieldId);
}
