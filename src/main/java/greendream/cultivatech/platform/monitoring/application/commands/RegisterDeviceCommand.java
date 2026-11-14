package greendream.cultivatech.platform.monitoring.application.commands;

/**
 * Command to register a device on a field.
 *
 * @param fieldId   identity of the field where the device is installed
 * @param macAddress hardware MAC address of the device
 * @param status     initial operational status
 */
public record RegisterDeviceCommand(Long fieldId, String macAddress, String status) {
}
