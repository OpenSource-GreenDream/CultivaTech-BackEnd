package greendream.cultivatech.platform.monitoring.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Date;

/**
 * HTTP request to register a reading produced by a device.
 */
public record RegisterSensorReadingRequest(
        @NotBlank @Size(max = 30) String parameter,
        @NotNull Double value,
        @NotBlank @Size(max = 10) String unit,
        @NotNull Date recordedAt
) {
}
