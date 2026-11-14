package greendream.cultivatech.platform.monitoring.application.commands;

/**
 * Command to delete a device.
 *
 * @param deviceId identity of the device
 */
public record DeleteDeviceCommand(Long deviceId) {
}
