package greendream.cultivatech.platform.shared.domain.exceptions;

import java.util.List;

public class InvalidSensorUnitException extends IllegalArgumentException {
    private final String parameter;
    private final String rejectedValue;
    private final String expectedUnit;

    public InvalidSensorUnitException(String parameter, String rejectedValue, String expectedUnit) {
        super("INVALID_SENSOR_UNIT");
        this.parameter = parameter;
        this.rejectedValue = rejectedValue;
        this.expectedUnit = expectedUnit;
    }

    public String getParameter() { return parameter; }
    public String getRejectedValue() { return rejectedValue; }
    public String getExpectedUnit() { return expectedUnit; }
    public List<String> getAllowedValues() { return List.of(expectedUnit); }
}
