package greendream.cultivatech.platform.monitoring.interfaces.rest.resources;

import java.util.Date;

/**
 * HTTP response representing a sensor reading.
 */
public record SensorReadingResource(
        Long id,
        Long deviceId,
        String parameter,
        double value,
        String unit,
        Date recordedAt
) {
}
