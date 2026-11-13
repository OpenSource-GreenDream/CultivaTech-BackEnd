package greendream.cultivatech.platform.iam.application.queries;

/**
 * Query to retrieve a single IAM role.
 *
 * @param roleId identity of the requested role
 */
public record GetRoleByIdQuery(Long roleId) {
}
