package greendream.cultivatech.platform.profile.domain.model.valueobjects;

/**
 * Size of a field, expressed in square meters.
 */
public record FieldSize(double value) {

    public FieldSize {
        if (Double.isNaN(value) || Double.isInfinite(value) || value <= 0.0) {
            throw new IllegalArgumentException("error.field.size.invalid");
        }
    }
}
