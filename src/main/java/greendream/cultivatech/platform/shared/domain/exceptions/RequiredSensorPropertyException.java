package greendream.cultivatech.platform.shared.domain.exceptions;

public class RequiredSensorPropertyException extends IllegalArgumentException {
    private final String errorCode;
    private final String field;

    public RequiredSensorPropertyException(String errorCode, String field) {
        super(errorCode);
        this.errorCode = errorCode;
        this.field = field;
    }

    public String getErrorCode() { return errorCode; }
    public String getField() { return field; }
}
