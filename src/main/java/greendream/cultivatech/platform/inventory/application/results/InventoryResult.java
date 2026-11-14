package greendream.cultivatech.platform.inventory.application.results;

/**
 * Application-level representation of an inventory entry.
 *
 * @param id            persisted inventory identity
 * @param profileId     profile that owns the entry
 * @param fieldId       field where the item is stored
 * @param stockQuantity number of available units
 */
public record InventoryResult(
        Long id,
        Long profileId,
        Long fieldId,
        int stockQuantity
) {
}
