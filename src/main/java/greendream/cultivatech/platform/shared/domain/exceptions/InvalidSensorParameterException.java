package greendream.cultivatech.platform.shared.domain.exceptions;

import java.util.List;

public class InvalidSensorParameterException extends IllegalArgumentException {
    private final String rejectedValue;
    private final List<String> allowedValues;

    public InvalidSensorParameterException(String rejectedValue, List<String> allowedValues) {
        super("INVALID_SENSOR_PARAMETER");
        this.rejectedValue = rejectedValue;
        this.allowedValues = List.copyOf(allowedValues);
    }

    public String getRejectedValue() { return rejectedValue; }
    public List<String> getAllowedValues() { return allowedValues; }
}
