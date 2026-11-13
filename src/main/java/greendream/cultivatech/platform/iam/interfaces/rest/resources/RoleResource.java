package greendream.cultivatech.platform.iam.interfaces.rest.resources;

/**
 * HTTP response for an IAM role.
 */
public record RoleResource(
        Long id,
        String name,
        String description
) {
}
