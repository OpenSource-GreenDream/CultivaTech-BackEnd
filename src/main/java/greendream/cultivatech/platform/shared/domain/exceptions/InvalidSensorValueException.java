package greendream.cultivatech.platform.shared.domain.exceptions;

public class InvalidSensorValueException extends IllegalArgumentException {
    private final String parameter;
    private final Object rejectedValue;
    private final Double minValue;
    private final Double maxValue;

    public InvalidSensorValueException(String parameter, Object rejectedValue, Double minValue, Double maxValue) {
        super("INVALID_SENSOR_VALUE");
        this.parameter = parameter;
        this.rejectedValue = rejectedValue;
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    public String getParameter() { return parameter; }
    public Object getRejectedValue() { return rejectedValue; }
    public Double getMinValue() { return minValue; }
    public Double getMaxValue() { return maxValue; }
}
