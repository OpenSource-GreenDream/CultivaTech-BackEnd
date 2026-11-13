package greendream.cultivatech.platform.profile.application.queries;

/**
 * Query to retrieve every field owned by a profile.
 *
 * @param profileId identity of the owning profile
 */
public record GetFieldsByProfileIdQuery(Long profileId) {
}
