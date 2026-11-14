package greendream.cultivatech.platform.inventory.application.queries;

/**
 * Query to retrieve every inventory entry owned by a profile.
 *
 * @param profileId identity of the profile
 */
public record GetInventoriesByProfileIdQuery(Long profileId) {
}
