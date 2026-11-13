package greendream.cultivatech.platform.profile.application.commands;

/**
 * Command to replace the editable data of an existing field.
 *
 * <p>The owning profile cannot change: a field always belongs to the profile that
 * created it.</p>
 *
 * @param fieldId   identity of the field to update
 * @param name      raw field name supplied by the caller
 * @param sizeM2    field size in square meters
 * @param soilType  raw soil type supplied by the caller
 * @param latitude  latitude in decimal degrees
 * @param longitude longitude in decimal degrees
 */
public record UpdateFieldCommand(
        Long fieldId,
        String name,
        double sizeM2,
        String soilType,
        double latitude,
        double longitude
) {
}
