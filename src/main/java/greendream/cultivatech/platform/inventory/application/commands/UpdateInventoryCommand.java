package greendream.cultivatech.platform.inventory.application.commands;

/**
 * Command to update the editable data of an inventory entry.
 *
 * @param inventoryId   identity of the inventory entry
 * @param fieldId       new field where the item is stored
 * @param stockQuantity new number of available units
 */
public record UpdateInventoryCommand(Long inventoryId, Long fieldId, Integer stockQuantity) {
}
