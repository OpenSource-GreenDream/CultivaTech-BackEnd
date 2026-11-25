package greendream.cultivatech.platform.inventory.application.internal.commandservices;

import greendream.cultivatech.platform.commercial.domain.model.aggregates.Product;
import greendream.cultivatech.platform.commercial.domain.repositories.ProductRepository;
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
import greendream.cultivatech.platform.profile.domain.model.aggregates.Field;
import greendream.cultivatech.platform.profile.domain.repositories.FieldRepository;
import greendream.cultivatech.platform.profile.domain.repositories.ProfileRepository;
import greendream.cultivatech.platform.shared.application.result.Result;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InventoryCommandServiceImpl implements InventoryCommandService {

    private final InventoryRepository inventoryRepository;
    private final ProfileRepository profileRepository;
    private final FieldRepository fieldRepository;
    private final ProductRepository productRepository;

    public InventoryCommandServiceImpl(InventoryRepository inventoryRepository,
                                       ProfileRepository profileRepository,
                                       FieldRepository fieldRepository,
                                       ProductRepository productRepository) {
        this.inventoryRepository = inventoryRepository;
        this.profileRepository = profileRepository;
        this.fieldRepository = fieldRepository;
        this.productRepository = productRepository;
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

        Field field = fieldRepository.findById(new greendream.cultivatech.platform.profile.domain.model.valueobjects.FieldId(fieldId.value())).orElse(null);
        if (field == null) {
            return Result.failure(new InventoryError("error.field.not-found"));
        }
        if (!field.profileId().value().equals(profileId.value())) {
            return Result.failure(new InventoryError("error.field.not-owned"));
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

        FieldId newFieldId;
        StockQuantity newStock;
        try {
            newFieldId = new FieldId(command.fieldId());
            newStock = new StockQuantity(command.stockQuantity());
        } catch (IllegalArgumentException exception) {
            return Result.failure(new InventoryError(exception.getMessage()));
        }

        Field field = fieldRepository.findById(new greendream.cultivatech.platform.profile.domain.model.valueobjects.FieldId(newFieldId.value())).orElse(null);
        if (field == null) {
            return Result.failure(new InventoryError("error.field.not-found"));
        }
        if (!field.profileId().value().equals(inventory.profileId().value())) {
            return Result.failure(new InventoryError("error.field.not-owned"));
        }

        Product product = productRepository.findByInventoryId(inventory.id().orElseThrow().value()).orElse(null);
        if (product != null && product.fieldId() != null && !product.fieldId().equals(newFieldId.value())) {
            return Result.failure(new InventoryError("error.inventory.field.conflict"));
        }

        try {
            inventory.changeField(newFieldId);
            inventory.changeStockQuantity(newStock);
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

        if (productRepository.findByInventoryId(inventoryId.value()).isPresent()) {
            return Result.failure(new InventoryError("error.inventory.in-use"));
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

    private greendream.cultivatech.platform.profile.domain.model.valueobjects.ProfileId profileReference(
            ProfileId profileId) {
        return new greendream.cultivatech.platform.profile.domain.model.valueobjects.ProfileId(profileId.value());
    }
}