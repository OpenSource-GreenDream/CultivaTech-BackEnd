package greendream.cultivatech.platform.monitoring.domain.model.enums;

import greendream.cultivatech.platform.shared.domain.exceptions.InvalidSensorParameterException;
import greendream.cultivatech.platform.shared.domain.exceptions.RequiredSensorPropertyException;

/**
 * Canonical agricultural telemetry parameters supported by the Monitoring context.
 *
 * <p>This enum is the single source of truth for telemetry parameters. Monitoring owns
 * it and Analytics reuses it. Persistence must store {@link #name()}, never the ordinal.</p>
 */
public enum SensorParameter {
    MOISTURE("%"),
    PH("unitless"),
    NITROGEN("mg/kg"),
    PHOSPHORUS("mg/kg"),
    POTASSIUM("mg/kg"),
    CALCIUM("mg/kg"),
    MAGNESIUM("mg/kg"),
    SULFUR("mg/kg");

    private final String canonicalUnit;

    SensorParameter(String canonicalUnit) {
        this.canonicalUnit = canonicalUnit;
    }

    public String getCanonicalUnit() {
        return canonicalUnit;
    }

    public boolean isValidValue(double value) {
        if (!Double.isFinite(value)) {
            return false;
        }
        return switch (this) {
            case MOISTURE -> value >= 0.0 && value <= 100.0;
            case PH -> value >= 0.0 && value <= 14.0;
            default -> value >= 0.0;
        };
    }

    public boolean isCompatibleUnit(String unit) {
        return canonicalUnit.equals(unit == null ? null : unit.trim());
    }

    public static SensorParameter from(String value) {
        if (value == null || value.isBlank()) {
            throw new RequiredSensorPropertyException("REQUIRED_SENSOR_PARAMETER", "parameter");
        }
        try {
            return SensorParameter.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException exception) {
            throw new InvalidSensorParameterException(
                    value,
                    java.util.Arrays.stream(SensorParameter.values()).map(Enum::name).toList()
            );
        }
    }
}
