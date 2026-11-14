package greendream.cultivatech.platform.monitoring.application.commands;

/**
 * Command to delete a sensor reading.
 *
 * @param sensorReadingId identity of the reading
 */
public record DeleteSensorReadingCommand(Long sensorReadingId) {
}
