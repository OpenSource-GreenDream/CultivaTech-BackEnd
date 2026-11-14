package greendream.cultivatech.platform.monitoring.domain.model.valueobjects;

import java.util.Locale;

/**
 * Operational status of a device.
 *
 * <p>The database diagram models {@code devices.status} as {@code VARCHAR(20)} and
 * does not define a controlled vocabulary. The value is therefore kept as free text,
 * trimmed and normalized to upper case, and its length is bounded to the column
 * size. If the domain vocabulary is defined later, this value object can be narrowed
 * without changing the aggregate.</p>
 */
public record DeviceStatus(String value) {

    private static final int MAX_LENGTH = 20;

    public DeviceStatus {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("error.device.status.required");
        }

        String normalized = value.trim().toUpperCase(Locale.ROOT);
        if (normalized.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("error.device.status.too-long");
        }

        value = normalized;
    }
}
