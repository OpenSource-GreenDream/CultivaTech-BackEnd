package greendream.cultivatech.platform.monitoring.application.commands;

import java.util.Date;

/**
 * Command to register a reading produced by a device.
 *
 * @param deviceId   device that produced the reading
 * @param parameter  measured parameter
 * @param value      measured value
 * @param unit       unit of the measured value
 * @param recordedAt instant reported by the device
 */
public record RegisterSensorReadingCommand(
        Long deviceId,
        String parameter,
        Double value,
        String unit,
        Date recordedAt
) {
}
