package greendream.cultivatech.platform.monitoring.domain.model.valueobjects;

/**
 * Reference to the field a device is installed on.
 *
 * <p>The Monitoring context owns this reference locally to stay independent from the
 * Profile bounded context. Translation from a Profile field identity happens in the
 * application layer, following the same approach as {@code Profile}'s local
 * {@code UserId}.</p>
 */
public record FieldId(Long value) {

    public FieldId {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("error.device.field-id.invalid");
        }
    }
}
