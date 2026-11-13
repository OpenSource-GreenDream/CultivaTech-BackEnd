package greendream.cultivatech.platform.profile.interfaces.rest.resources;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * HTTP request to replace the editable data of a field.
 */
public record UpdateFieldRequest(
        @NotBlank @Size(max = 100) String name,
        @NotNull @DecimalMin(value = "0.0", inclusive = false) Double sizeM2,
        @NotBlank @Size(max = 50) String soilType,
        @NotNull @DecimalMin("-90.0") @DecimalMax("90.0") Double latitude,
        @NotNull @DecimalMin("-180.0") @DecimalMax("180.0") Double longitude
) {
}
