package greendream.cultivatech.platform.profile.application.commands;

/**
 * Command to create a field owned by a profile.
 *
 * @param profileId owning profile identifier
 * @param name      raw field name supplied by the caller
 * @param sizeM2    field size in square meters
 * @param soilType  raw soil type supplied by the caller
 * @param latitude  latitude in decimal degrees
 * @param longitude longitude in decimal degrees
 */
public record CreateFieldCommand(
        Long profileId,
        String name,
        double sizeM2,
        String soilType,
        double latitude,
        double longitude
) {
}
