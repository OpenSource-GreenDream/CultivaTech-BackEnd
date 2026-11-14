package greendream.cultivatech.platform.monitoring.application.commands;

/**
 * Command to update the operational status of a device.
 *
 * @param deviceId identity of the device
 * @param status   new operational status
 */
public record UpdateDeviceCommand(Long deviceId, String status) {
}
