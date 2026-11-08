package greendream.cultivatech.platform.iam.application.results;

/**
 * Result of registering the profile of a signed-up user.
 *
 * @param fundoName            normalized fundo name
 * @param contactPhone         normalized contact phone
 * @param moistureThreshold    moisture threshold percentage
 * @param temperatureThreshold temperature threshold in degrees Celsius
 */
public record ProfileRegistrationResult(
        String fundoName,
        String contactPhone,
        double moistureThreshold,
        double temperatureThreshold
) {
}
