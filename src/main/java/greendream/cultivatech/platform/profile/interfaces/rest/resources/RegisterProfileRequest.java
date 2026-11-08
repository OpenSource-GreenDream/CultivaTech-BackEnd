package greendream.cultivatech.platform.profile.interfaces.rest.resources;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * HTTP request to register the profile of a user.
 */
public record RegisterProfileRequest(
        @NotBlank @Size(max = 120) String fundoName,
        @NotBlank @Size(max = 20) @Pattern(regexp = "^\\+?[0-9()\\-\\s]+$") String contactPhone,
        @NotNull @DecimalMin("0.0") @DecimalMax("100.0") Double moistureThreshold,
        @NotNull @DecimalMin("-20.0") @DecimalMax("60.0") Double temperatureThreshold
) {
}
