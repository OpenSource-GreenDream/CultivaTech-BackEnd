package greendream.cultivatech.platform.profile.domain.model.valueobjects;

/**
 * Longitude of a field, expressed in decimal degrees.
 */
public record Longitude(double value) {

    public static final double MIN = -180.0;
    public static final double MAX = 180.0;

    public Longitude {
        if (Double.isNaN(value) || Double.isInfinite(value) || value < MIN || value > MAX) {
            throw new IllegalArgumentException("error.field.longitude.invalid");
        }
    }
}
