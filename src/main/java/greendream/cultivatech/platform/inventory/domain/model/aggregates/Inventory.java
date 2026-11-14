package greendream.cultivatech.platform.inventory.domain.model.aggregates;

import greendream.cultivatech.platform.inventory.domain.model.valueobjects.FieldId;
import greendream.cultivatech.platform.inventory.domain.model.valueobjects.InventoryId;
import greendream.cultivatech.platform.inventory.domain.model.valueobjects.ProfileId;
import greendream.cultivatech.platform.inventory.domain.model.valueobjects.StockQuantity;
import greendream.cultivatech.platform.shared.domain.aggregates.AbstractDomainAggregateRoot;

import java.util.Objects;
import java.util.Optional;

/**
 * Inventory aggregate root.
 * Represents an item that a profile keeps on a field. The inventory entry is the
 * source of truth: a catalog product is generated from it, and the product keeps a
 * reference back to this aggregate. Other bounded contexts are referenced through
 * local identities so the aggregate stays isolated from their persistence models.
 */
public final class Inventory extends AbstractDomainAggregateRoot<Inventory> {

    private InventoryId id;
    private final ProfileId profileId;
    private FieldId fieldId;
    private StockQuantity stockQuantity;

    private Inventory(InventoryId id,
                      ProfileId profileId,
                      FieldId fieldId,
                      StockQuantity stockQuantity) {
        this.id = id;
        this.profileId = Objects.requireNonNull(profileId, "profileId must not be null");
        this.fieldId = Objects.requireNonNull(fieldId, "fieldId must not be null");
        this.stockQuantity = Objects.requireNonNull(stockQuantity, "stockQuantity must not be null");
    }

    /**
     * Registers a new inventory entry that has not been persisted yet.
     */
    public static Inventory register(ProfileId profileId,
                                     FieldId fieldId,
                                     StockQuantity stockQuantity) {
        return new Inventory(null, profileId, fieldId, stockQuantity);
    }

    /**
     * Rebuilds a persisted inventory entry.
     */
    public static Inventory reconstitute(InventoryId id,
                                         ProfileId profileId,
                                         FieldId fieldId,
                                         StockQuantity stockQuantity) {
        return new Inventory(
                Objects.requireNonNull(id, "id must not be null"),
                profileId,
                fieldId,
                stockQuantity
        );
    }

    public Optional<InventoryId> id() {
        return Optional.ofNullable(id);
    }

    public ProfileId profileId() {
        return profileId;
    }

    public FieldId fieldId() {
        return fieldId;
    }

    public StockQuantity stockQuantity() {
        return stockQuantity;
    }

    /**
     * @return true when this entry belongs to the given profile
     */
    public boolean belongsTo(ProfileId profileId) {
        return this.profileId.equals(Objects.requireNonNull(profileId, "profileId must not be null"));
    }

    /**
     * @return true when this entry is stored on the given field
     */
    public boolean isStoredAt(FieldId fieldId) {
        return this.fieldId.equals(Objects.requireNonNull(fieldId, "fieldId must not be null"));
    }

    public void changeField(FieldId newFieldId) {
        this.fieldId = Objects.requireNonNull(newFieldId, "fieldId must not be null");
    }

    public void changeStockQuantity(StockQuantity newStockQuantity) {
        this.stockQuantity = Objects.requireNonNull(newStockQuantity, "stockQuantity must not be null");
    }
}
