package greendream.cultivatech.platform.profile.interfaces.rest.resources;

/**
 * HTTP response representing a field.
 */
public record FieldResource(
        Long id,
        Long profileId,
        String name,
        double sizeM2,
        String soilType,
        double latitude,
        double longitude
) {
}
