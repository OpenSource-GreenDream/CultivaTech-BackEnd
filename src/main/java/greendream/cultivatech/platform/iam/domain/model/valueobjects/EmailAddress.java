package greendream.cultivatech.platform.iam.domain.model.valueobjects;

import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Validated and normalized email address of a user.
 *
 * <p>Normalization is intentionally part of the value object: emails are trimmed and
 * lower-cased so that uniqueness checks and lookups are case-insensitive.</p>
 */
public record EmailAddress(String value) {

    private static final int MAX_LENGTH = 255;
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    public EmailAddress {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("error.user.email.required");
        }

        String normalized = value.trim().toLowerCase(Locale.ROOT);
        if (normalized.length() > MAX_LENGTH || !EMAIL_PATTERN.matcher(normalized).matches()) {
            throw new IllegalArgumentException("error.user.email.invalid");
        }

        value = normalized;
    }
}
