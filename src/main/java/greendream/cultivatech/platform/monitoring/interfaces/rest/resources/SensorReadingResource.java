package greendream.cultivatech.platform.monitoring.interfaces.rest.resources;

import greendream.cultivatech.platform.monitoring.domain.model.enums.SensorParameter;
import java.util.Date;

/**
 * HTTP response representing a sensor reading.
 */
public record SensorReadingResource(
        Long id,
        Long deviceId,
        SensorParameter parameter,
        double value,
        String unit,
        Date recordedAt
) {
}
