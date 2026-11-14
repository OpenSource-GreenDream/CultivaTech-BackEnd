package greendream.cultivatech.platform.monitoring.application.queries;

/**
 * Query to retrieve a device by its identity.
 *
 * @param deviceId identity of the device
 */
public record GetDeviceByIdQuery(Long deviceId) {
}
