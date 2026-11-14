package greendream.cultivatech.platform.monitoring.application.results;

import java.util.Date;

/**
 * Application-level representation of a sensor reading.
 *
 * @param id         persisted reading identity
 * @param deviceId   device that produced the reading
 * @param parameter  measured parameter
 * @param value      measured value
 * @param unit       unit of the measured value
 * @param recordedAt instant reported by the device
 */
public record SensorReadingResult(
        Long id,
        Long deviceId,
        String parameter,
        double value,
        String unit,
        Date recordedAt
) {
}
