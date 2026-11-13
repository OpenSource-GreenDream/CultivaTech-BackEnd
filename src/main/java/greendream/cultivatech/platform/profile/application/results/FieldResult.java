package greendream.cultivatech.platform.profile.application.results;

/**
 * Application-level representation of a field.
 *
 * @param id        persisted field identity
 * @param profileId owning profile identifier
 * @param name      normalized field name
 * @param sizeM2    field size in square meters
 * @param soilType  normalized soil type
 * @param latitude  latitude in decimal degrees
 * @param longitude longitude in decimal degrees
 */
public record FieldResult(
        Long id,
        Long profileId,
        String name,
        double sizeM2,
        String soilType,
        double latitude,
        double longitude
) {
}
