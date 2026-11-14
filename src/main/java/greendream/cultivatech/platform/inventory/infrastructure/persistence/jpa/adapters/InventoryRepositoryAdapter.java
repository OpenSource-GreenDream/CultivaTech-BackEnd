package greendream.cultivatech.platform.inventory.infrastructure.persistence.jpa.adapters;

import greendream.cultivatech.platform.inventory.domain.model.aggregates.Inventory;
import greendream.cultivatech.platform.inventory.domain.model.valueobjects.FieldId;
import greendream.cultivatech.platform.inventory.domain.model.valueobjects.InventoryId;
import greendream.cultivatech.platform.inventory.domain.model.valueobjects.ProfileId;
import greendream.cultivatech.platform.inventory.domain.repositories.InventoryRepository;
import greendream.cultivatech.platform.inventory.infrastructure.persistence.jpa.entities.InventoryPersistenceEntity;
import greendream.cultivatech.platform.inventory.infrastructure.persistence.jpa.mappers.InventoryMapper;
import greendream.cultivatech.platform.inventory.infrastructure.persistence.jpa.repositories.InventoryJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * JPA adapter implementing the InventoryRepository domain port.
 */
@Repository
public class InventoryRepositoryAdapter implements InventoryRepository {

    private final InventoryJpaRepository inventoryJpaRepository;
    private final InventoryMapper mapper;

    public InventoryRepositoryAdapter(InventoryJpaRepository inventoryJpaRepository,
                                      InventoryMapper mapper) {
        this.inventoryJpaRepository = inventoryJpaRepository;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public Inventory save(Inventory inventory) {
        InventoryPersistenceEntity entity = inventory.id()
                .map(this::findEntity)
                .orElseGet(() -> mapper.toNewEntity(inventory));

        if (inventory.id().isPresent()) {
            mapper.updateEntity(entity, inventory);
        }

        return mapper.toDomain(inventoryJpaRepository.save(entity));
    }

    @Override
    @Transactional
    public Inventory update(Inventory inventory) {
        if (inventory.id().isEmpty()) {
            throw new IllegalArgumentException("error.inventory.id.not-assigned");
        }
        return save(inventory);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Inventory> findAll() {
        return inventoryJpaRepository.findAll().stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Inventory> findById(InventoryId id) {
        return inventoryJpaRepository.findById(id.value())
                .map(mapper::toDomain);
    }

    @Override
    @Transactional
    public void deleteById(InventoryId id) {
        inventoryJpaRepository.delete(findEntity(id));
    }

    @Override
    @Transactional
    public void deleteAll() {
        inventoryJpaRepository.deleteAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Inventory> findByProfileId(ProfileId profileId) {
        return inventoryJpaRepository.findByProfileId(profileId.value()).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Inventory> findByFieldId(FieldId fieldId) {
        return inventoryJpaRepository.findByFieldId(fieldId.value()).stream()
                .map(mapper::toDomain)
                .toList();
    }

    private InventoryPersistenceEntity findEntity(InventoryId id) {
        return inventoryJpaRepository.findById(id.value())
                .orElseThrow(() -> new IllegalArgumentException("error.inventory.not-found"));
    }
}
