package greendream.cultivatech.platform.profile.application.commands;

/**
 * Command to replace the editable data of an existing profile.
 *
 * @param userId              owning IAM user identifier
 * @param fundoName           raw fundo name supplied by the caller
 * @param contactPhone        raw contact phone supplied by the caller
 * @param moistureThreshold   moisture threshold percentage
 * @param temperatureThreshold temperature threshold in degrees Celsius
 */
public record UpdateProfileCommand(
        Long userId,
        String fundoName,
        String contactPhone,
        double moistureThreshold,
        double temperatureThreshold
) {
}
