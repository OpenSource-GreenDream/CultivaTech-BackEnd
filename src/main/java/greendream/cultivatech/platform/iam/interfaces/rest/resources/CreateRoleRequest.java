package greendream.cultivatech.platform.iam.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * HTTP request to create an IAM role.
 */
public record CreateRoleRequest(
        @NotBlank @Size(max = 50) String name,
        @Size(max = 255) String description
) {
}
