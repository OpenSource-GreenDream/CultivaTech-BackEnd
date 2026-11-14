package greendream.cultivatech.platform.inventory.application.commands;

/**
 * Command to delete an inventory entry.
 *
 * @param inventoryId identity of the inventory entry
 */
public record DeleteInventoryCommand(Long inventoryId) {
}
