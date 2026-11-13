package greendream.cultivatech.platform.profile.interfaces.rest.resources;

/**
 * HTTP response representing a profile.
 */
public record ProfileResource(
        Long id,
        Long userId,
        String fundoName,
        String contactPhone,
        double moistureThreshold,
        double temperatureThreshold
) {
}
