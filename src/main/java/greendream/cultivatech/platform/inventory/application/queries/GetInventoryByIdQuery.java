package greendream.cultivatech.platform.inventory.application.queries;

/**
 * Query to retrieve an inventory entry by its identity.
 *
 * @param inventoryId identity of the inventory entry
 */
public record GetInventoryByIdQuery(Long inventoryId) {
}
