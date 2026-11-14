package greendream.cultivatech.platform.inventory.domain.model.valueobjects;

/**
 * Identity of an inventory row.
 */
public record InventoryId(Long value) {

    public InventoryId {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("error.inventory.id.invalid");
        }
    }
}
