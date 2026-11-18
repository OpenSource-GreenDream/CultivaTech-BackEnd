package greendream.cultivatech.platform.iam.interfaces.rest.resources;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * HTTP request for sign-in.
 *
 * @param email    registered email address
 * @param password raw password supplied by the caller
 */
public record SignInRequest(
        @NotBlank @Email String email,
        @NotBlank @Size(max = 128) String password
) {
}
