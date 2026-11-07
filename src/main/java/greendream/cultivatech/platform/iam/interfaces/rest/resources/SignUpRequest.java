package greendream.cultivatech.platform.iam.interfaces.rest.resources;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * HTTP request for sign-up.
 */
public record SignUpRequest(
        @NotBlank @Email String email,
        @NotBlank @Size(min = 8, max = 128) String password
) {
}
