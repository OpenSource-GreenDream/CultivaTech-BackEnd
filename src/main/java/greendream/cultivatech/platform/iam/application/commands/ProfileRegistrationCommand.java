package greendream.cultivatech.platform.iam.application.commands;

/**
 * Command to register the profile associated with a newly signed-up user.
 *
 * @param userId               persisted IAM user identifier
 * @param fundoName            fundo name for the new profile
 * @param contactPhone         contact phone for the new profile
 * @param moistureThreshold    moisture threshold percentage
 * @param temperatureThreshold temperature threshold in degrees Celsius
 */
public record ProfileRegistrationCommand(
        Long userId,
        String fundoName,
        String contactPhone,
        double moistureThreshold,
        double temperatureThreshold
) {
}
