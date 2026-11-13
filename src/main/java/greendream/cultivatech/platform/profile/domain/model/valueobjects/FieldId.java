package greendream.cultivatech.platform.profile.domain.model.valueobjects;

/**
 * Identity of a field.
 */
public record FieldId(Long value) {

    public FieldId {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("error.field.id.invalid");
        }
    }
}
