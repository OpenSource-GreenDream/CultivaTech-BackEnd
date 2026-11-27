package greendream.cultivatech.platform.shared.domain.exceptions;

public class InvalidSensorTimestampException extends IllegalArgumentException {
    public InvalidSensorTimestampException() {
        super("INVALID_SENSOR_TIMESTAMP");
    }
}
