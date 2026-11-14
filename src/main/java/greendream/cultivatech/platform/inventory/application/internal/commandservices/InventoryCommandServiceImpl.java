package greendream.cultivatech.platform.inventory.application.internal.commandservices;

import greendream.cultivatech.platform.inventory.application.commands.DeleteInventoryCommand;
import greendream.cultivatech.platform.inventory.application.commands.RegisterInventoryCommand;
import greendream.cultivatech.platform.inventory.application.commands.UpdateInventoryCommand;
import greendream.cultivatech.platform.inventory.application.commandservices.InventoryCommandService;
import greendream.cultivatech.platform.inventory.application.results.InventoryError;
import greendream.cultivatech.platform.inventory.application.results.InventoryResult;
import greendream.cultivatech.platform.inventory.domain.model.aggregates.Inventory;
import greendream.cultivatech.platform.inventory.domain.model.valueobjects.FieldId;
import greendream.cultivatech.platform.inventory.domain.model.valueobjects.InventoryId;
import greendream.cultivatech.platform.inventory.domain.model.valueobjects.ProfileId;
import greendream.cultivatech.platform.inventory.domain.model.valueobjects.StockQuantity;
import greendream.cultivatech.platform.inventory.domain.repositories.InventoryRepository;
import greendream.cultivatech.platform.profile.domain.repositories.ProfileRepository;
import greendream.cultivatech.platform.shared.application.result.Result;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of InventoryCommandService
 */
@Service
public class InventoryCommandServiceImpl implements InventoryCommandService {

    private final InventoryRepository inventoryRepository;
    private final ProfileRepository profileRepository;

    public InventoryCommandServiceImpl(InventoryRepository inventoryRepository,
                                       ProfileRepository profileRepository) {
        this.inventoryRepository = inventoryRepository;
        this.profileRepository = profileRepository;
    }

    @Override
    @Transactional
    public Result<InventoryResult, InventoryError> register(RegisterInventoryCommand command) {
        if (command == null) {
            return Result.failure(new InventoryError("error.command.required"));
        }

        ProfileId profileId;
        FieldId fieldId;
        StockQuantity stockQuantity;
        try {
            profileId = new ProfileId(command.profileId());
            fieldId = new FieldId(command.fieldId());
            stockQuantity = new StockQuantity(command.stockQuantity());
        } catch (IllegalArgumentException exception) {
            return Result.failure(new InventoryError(exception.getMessage()));
        }

        if (profileRepository.findById(profileReference(profileId)).isEmpty()) {
            return Result.failure(new InventoryError("error.profile.not-found"));
        }

        try {
            Inventory inventory = Inventory.register(profileId, fieldId, stockQuantity);
            return Result.success(toResult(inventoryRepository.save(inventory)));
        } catch (IllegalArgumentException exception) {
            return Result.failure(new InventoryError(exception.getMessage()));
        }
    }

    @Override
    @Transactional
    public Result<InventoryResult, InventoryError> update(UpdateInventoryCommand command) {
        if (command == null) {
            return Result.failure(new InventoryError("error.command.required"));
        }

        InventoryId inventoryId;
        try {
            inventoryId = new InventoryId(command.inventoryId());
        } catch (IllegalArgumentException exception) {
            return Result.failure(new InventoryError(exception.getMessage()));
        }

        Inventory inventory = inventoryRepository.findById(inventoryId).orElse(null);
        if (inventory == null) {
            return Result.failure(new InventoryError("error.inventory.not-found"));
        }

        try {
            inventory.changeField(new FieldId(command.fieldId()));
            inventory.changeStockQuantity(new StockQuantity(command.stockQuantity()));
            return Result.success(toResult(inventoryRepository.update(inventory)));
        } catch (IllegalArgumentException exception) {
            return Result.failure(new InventoryError(exception.getMessage()));
        }
    }

    @Override
    @Transactional
    public Result<Void, InventoryError> delete(DeleteInventoryCommand command) {
        if (command == null) {
            return Result.failure(new InventoryError("error.command.required"));
        }

        InventoryId inventoryId;
        try {
            inventoryId = new InventoryId(command.inventoryId());
        } catch (IllegalArgumentException exception) {
            return Result.failure(new InventoryError(exception.getMessage()));
        }

        if (inventoryRepository.findById(inventoryId).isEmpty()) {
            return Result.failure(new InventoryError("error.inventory.not-found"));
        }

        inventoryRepository.deleteById(inventoryId);
        return Result.success(null);
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

    /**
     * Translates the local inventory profile reference into the Profile context
     * identity used by {@link ProfileRepository}.
     */
    private greendream.cultivatech.platform.profile.domain.model.valueobjects.ProfileId profileReference(
            ProfileId profileId) {
        return new greendream.cultivatech.platform.profile.domain.model.valueobjects.ProfileId(profileId.value());
    }
}
