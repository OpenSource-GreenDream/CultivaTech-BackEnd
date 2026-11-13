package greendream.cultivatech.platform.profile.domain.model.valueobjects;

/**
 * Temperature threshold configured by a profile, expressed in degrees Celsius.
 */
public record TemperatureThreshold(double value) {

    public static final double MIN = -20.0;
    public static final double MAX = 60.0;

    public TemperatureThreshold {
        if (Double.isNaN(value) || Double.isInfinite(value) || value < MIN || value > MAX) {
            throw new IllegalArgumentException("error.profile.temperature-threshold.invalid");
        }
    }
}
