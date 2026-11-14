package greendream.cultivatech.platform.inventory.application.queries;

/**
 * Query to retrieve every inventory entry stored on a field.
 *
 * @param fieldId identity of the field
 */
public record GetInventoriesByFieldIdQuery(Long fieldId) {
}
