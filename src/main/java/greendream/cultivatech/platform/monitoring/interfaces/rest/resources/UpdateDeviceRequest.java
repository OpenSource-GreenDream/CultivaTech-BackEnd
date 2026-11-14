package greendream.cultivatech.platform.monitoring.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * HTTP request to update the operational status of a device.
 */
public record UpdateDeviceRequest(
        @NotBlank @Size(max = 20) String status
) {
}
