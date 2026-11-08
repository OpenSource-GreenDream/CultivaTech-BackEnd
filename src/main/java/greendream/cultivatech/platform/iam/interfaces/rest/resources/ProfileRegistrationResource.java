package greendream.cultivatech.platform.iam.interfaces.rest.resources;

/**
 * HTTP profile payload returned by sign-up.
 */
public record ProfileRegistrationResource(
        String fundoName,
        String contactPhone,
        double moistureThreshold,
        double temperatureThreshold
) {
}
