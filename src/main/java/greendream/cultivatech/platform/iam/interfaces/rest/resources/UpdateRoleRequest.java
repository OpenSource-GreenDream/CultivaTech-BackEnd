package greendream.cultivatech.platform.iam.interfaces.rest.resources;

import jakarta.validation.constraints.Size;

/**
 * HTTP request to update an IAM role. Only the description is mutable.
 */
public record UpdateRoleRequest(
        @Size(max = 255) String description
) {
}
