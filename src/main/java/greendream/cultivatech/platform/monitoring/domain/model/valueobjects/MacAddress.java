package greendream.cultivatech.platform.monitoring.domain.model.valueobjects;

import java.util.Locale;

/**
 * MAC address of a device.
 *
 * <p>The value is normalized to upper case so that uniqueness checks are
 * case-insensitive regardless of the database collation. Its length is bounded to the
 * {@code VARCHAR(17)} column of the database diagram.</p>
 */
public record MacAddress(String value) {

    private static final int MAX_LENGTH = 17;

    public MacAddress {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("error.device.mac-address.required");
        }

        String normalized = value.trim().toUpperCase(Locale.ROOT);
        if (normalized.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("error.device.mac-address.too-long");
        }

        value = normalized;
    }
}
