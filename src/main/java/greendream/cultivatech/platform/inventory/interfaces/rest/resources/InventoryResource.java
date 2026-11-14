package greendream.cultivatech.platform.inventory.interfaces.rest.resources;

/**
 * HTTP response representing an inventory entry.
 */
public record InventoryResource(
        Long id,
        Long profileId,
        Long fieldId,
        int stockQuantity
) {
}
