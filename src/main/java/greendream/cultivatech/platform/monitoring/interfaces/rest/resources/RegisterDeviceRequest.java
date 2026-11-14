package greendream.cultivatech.platform.monitoring.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * HTTP request to register a device on a field.
 */
public record RegisterDeviceRequest(
        @NotBlank @Size(max = 17) String macAddress,
        @NotBlank @Size(max = 20) String status
) {
}
