package greendream.cultivatech.platform.inventory.application.results;

/**
 * Application-level error returned by the inventory use cases.
 *
 * @param messageKey i18n message key describing the failure
 */
public record InventoryError(String messageKey) {
}
