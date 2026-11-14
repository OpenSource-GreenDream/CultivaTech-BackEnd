package greendream.cultivatech.platform.monitoring.domain.model.valueobjects;

/**
 * Identity of a device.
 */
public record DeviceId(Long value) {

    public DeviceId {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("error.device.id.invalid");
        }
    }
}
