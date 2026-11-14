package greendream.cultivatech.platform.inventory.application.internal.queryservices;

import greendream.cultivatech.platform.inventory.application.queries.GetAllInventoriesQuery;
import greendream.cultivatech.platform.inventory.application.queries.GetInventoriesByFieldIdQuery;
import greendream.cultivatech.platform.inventory.application.queries.GetInventoriesByProfileIdQuery;
import greendream.cultivatech.platform.inventory.application.queries.GetInventoryByIdQuery;
import greendream.cultivatech.platform.inventory.application.querieservices.InventoryQueryService;
import greendream.cultivatech.platform.inventory.application.results.InventoryError;
import greendream.cultivatech.platform.inventory.application.results.InventoryResult;
import greendream.cultivatech.platform.inventory.domain.model.aggregates.Inventory;
import greendream.cultivatech.platform.inventory.domain.model.valueobjects.FieldId;
import greendream.cultivatech.platform.inventory.domain.model.valueobjects.InventoryId;
import greendream.cultivatech.platform.inventory.domain.model.valueobjects.ProfileId;
import greendream.cultivatech.platform.inventory.domain.repositories.InventoryRepository;
import greendream.cultivatech.platform.shared.application.result.Result;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementation of InventoryQueryService
 */
@Service
public class InventoryQueryServiceImpl implements InventoryQueryService {

    private final InventoryRepository inventoryRepository;

    public InventoryQueryServiceImpl(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Result<List<InventoryResult>, InventoryError> getAllInventories(GetAllInventoriesQuery query) {
        if (query == null) {
            return Result.failure(new InventoryError("error.command.required"));
        }

        List<InventoryResult> inventories = inventoryRepository.findAll().stream()
                .map(this::toResult)
                .toList();
        return Result.success(inventories);
    }

    @Override
    @Transactional(readOnly = true)
    public Result<InventoryResult, InventoryError> getById(GetInventoryByIdQuery query) {
        if (query == null) {
            return Result.failure(new InventoryError("error.command.required"));
        }

        InventoryId inventoryId;
        try {
            inventoryId = new InventoryId(query.inventoryId());
        } catch (IllegalArgumentException exception) {
            return Result.failure(new InventoryError(exception.getMessage()));
        }

        Inventory inventory = inventoryRepository.findById(inventoryId).orElse(null);
        if (inventory == null) {
            return Result.failure(new InventoryError("error.inventory.not-found"));
        }

        return Result.success(toResult(inventory));
    }

    @Override
    @Transactional(readOnly = true)
    public Result<List<InventoryResult>, InventoryError> getByProfileId(GetInventoriesByProfileIdQuery query) {
        if (query == null) {
            return Result.failure(new InventoryError("error.command.required"));
        }

        ProfileId profileId;
        try {
            profileId = new ProfileId(query.profileId());
        } catch (IllegalArgumentException exception) {
            return Result.failure(new InventoryError(exception.getMessage()));
        }

        List<InventoryResult> inventories = inventoryRepository.findByProfileId(profileId).stream()
                .map(this::toResult)
                .toList();
        return Result.success(inventories);
    }

    @Override
    @Transactional(readOnly = true)
    public Result<List<InventoryResult>, InventoryError> getByFieldId(GetInventoriesByFieldIdQuery query) {
        if (query == null) {
            return Result.failure(new InventoryError("error.command.required"));
        }

        FieldId fieldId;
        try {
            fieldId = new FieldId(query.fieldId());
        } catch (IllegalArgumentException exception) {
            return Result.failure(new InventoryError(exception.getMessage()));
        }

        List<InventoryResult> inventories = inventoryRepository.findByFieldId(fieldId).stream()
                .map(this::toResult)
                .toList();
        return Result.success(inventories);
    }

    private InventoryResult toResult(Inventory inventory) {
        Long id = inventory.id()
                .map(InventoryId::value)
                .orElseThrow(() -> new IllegalArgumentException("error.inventory.id.not-assigned"));

        return new InventoryResult(
                id,
                inventory.profileId().value(),
                inventory.fieldId().value(),
                inventory.stockQuantity().value()
        );
    }
}
