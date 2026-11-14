package greendream.cultivatech.platform.inventory.application.commands;

/**
 * Command to register an inventory entry for a profile.
 *
 * @param profileId     identity of the profile that owns the entry
 * @param fieldId       identity of the field where the item is stored
 * @param stockQuantity initial number of available units
 */
public record RegisterInventoryCommand(Long profileId, Long fieldId, Integer stockQuantity) {
}
