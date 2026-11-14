package greendream.cultivatech.platform.monitoring.application.queries;

/**
 * Query to retrieve every device installed on a field.
 *
 * @param fieldId identity of the field
 */
public record GetDevicesByFieldIdQuery(Long fieldId) {
}
