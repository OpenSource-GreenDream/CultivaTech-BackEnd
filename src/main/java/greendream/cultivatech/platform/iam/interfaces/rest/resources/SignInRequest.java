package greendream.cultivatech.platform.iam.interfaces.rest.resources;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * HTTP request for sign-in.
 */
public record SignInRequest(
        @NotBlank @Email String email,
        @NotBlank String password
) {
}
