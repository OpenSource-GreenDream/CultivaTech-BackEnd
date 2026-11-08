package greendream.cultivatech.platform.profile.application.queries;

/**
 * Query to retrieve the profile owned by a user.
 *
 * @param userId owning IAM user identifier
 */
public record GetProfileByUserIdQuery(Long userId) {
}
