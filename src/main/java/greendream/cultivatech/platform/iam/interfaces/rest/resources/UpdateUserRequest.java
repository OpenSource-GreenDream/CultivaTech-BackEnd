package greendream.cultivatech.platform.iam.interfaces.rest.resources;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Set;

/**
 * HTTP request to update an IAM user.
 * A blank password and a null role set both mean "leave unchanged".
 */
public record UpdateUserRequest(
        @NotBlank @Email String email,
        @Size(max = 128) String password,
        Set<Long> roleIds
) {
}
