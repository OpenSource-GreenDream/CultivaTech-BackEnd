package greendream.cultivatech.platform.monitoring.application.queries;

/**
 * Query to retrieve every reading recorded by a device.
 *
 * @param deviceId identity of the device
 */
public record GetSensorReadingsByDeviceIdQuery(Long deviceId) {
}
