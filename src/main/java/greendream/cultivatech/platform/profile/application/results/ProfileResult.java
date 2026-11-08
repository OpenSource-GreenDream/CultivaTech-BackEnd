package greendream.cultivatech.platform.profile.application.results;

/**
 * Application-level representation of a profile.
 *
 * @param id                   persisted profile identity
 * @param userId               owning IAM user identifier
 * @param fundoName            normalized fundo name
 * @param contactPhone         normalized contact phone
 * @param moistureThreshold    moisture threshold percentage
 * @param temperatureThreshold temperature threshold in degrees Celsius
 */
public record ProfileResult(
        Long id,
        Long userId,
        String fundoName,
        String contactPhone,
        double moistureThreshold,
        double temperatureThreshold
) {
}
