package greendream.cultivatech.platform.profile.application.commands;

/**
 * Command to register the profile of a user.
 *
 * @param userId              owning IAM user identifier
 * @param fundoName           raw fundo name supplied by the caller
 * @param contactPhone        raw contact phone supplied by the caller
 * @param moistureThreshold   moisture threshold percentage
 * @param temperatureThreshold temperature threshold in degrees Celsius
 */
public record RegisterProfileCommand(
        Long userId,
        String fundoName,
        String contactPhone,
        double moistureThreshold,
        double temperatureThreshold
) {
}
