package greendream.cultivatech.platform.profile.domain.model.valueobjects;

/**
 * Latitude of a field, expressed in decimal degrees.
 */
public record Latitude(double value) {

    public static final double MIN = -90.0;
    public static final double MAX = 90.0;

    public Latitude {
        if (Double.isNaN(value) || Double.isInfinite(value) || value < MIN || value > MAX) {
            throw new IllegalArgumentException("error.field.latitude.invalid");
        }
    }
}
