package greendream.cultivatech.platform.monitoring.interfaces.rest.resources;

import greendream.cultivatech.platform.monitoring.domain.model.enums.SensorParameter;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Date;

/**
 * HTTP request to register a reading produced by a device.
 */
public record RegisterSensorReadingRequest(
        @NotNull SensorParameter parameter,
        @NotNull Double value,
        @NotBlank @Size(max = 10) String unit,
        @NotNull Date recordedAt
) {
}
