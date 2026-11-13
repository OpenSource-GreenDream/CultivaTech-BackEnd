package greendream.cultivatech.platform.iam.application.queries;

/**
 * Query to retrieve a single IAM user.
 *
 * @param userId identity of the requested user
 */
public record GetUserByIdQuery(Long userId) {
}
