package greendream.cultivatech.platform.profile.domain.model.valueobjects;

/**
 * Moisture threshold configured by a profile, expressed as a percentage.
 */
public record MoistureThreshold(double value) {

    public static final double MIN = 0.0;
    public static final double MAX = 100.0;

    public MoistureThreshold {
        if (Double.isNaN(value) || Double.isInfinite(value) || value < MIN || value > MAX) {
            throw new IllegalArgumentException("error.profile.moisture-threshold.invalid");
        }
    }
}
