package greendream.cultivatech.platform.profile.domain.model.valueobjects;

import java.util.regex.Pattern;

/**
 * Contact phone number of a profile.
 *
 * <p>The value is kept in international-friendly free form: an optional leading plus
 * sign, digits and the usual visual separators. Validation only guarantees that the
 * input is a plausible phone number and contains between 6 and 15 digits.</p>
 */
public record ContactPhone(String value) {

    private static final int MAX_LENGTH = 20;
    private static final int MIN_DIGITS = 6;
    private static final int MAX_DIGITS = 15;
    private static final Pattern ALLOWED_CHARACTERS = Pattern.compile("^\\+?[0-9()\\-\\s]+$");

    public ContactPhone {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("error.profile.contact-phone.required");
        }

        String normalized = value.trim();
        long digitCount = normalized.chars().filter(Character::isDigit).count();
        boolean valid = normalized.length() <= MAX_LENGTH
                && ALLOWED_CHARACTERS.matcher(normalized).matches()
                && digitCount >= MIN_DIGITS
                && digitCount <= MAX_DIGITS;
        if (!valid) {
            throw new IllegalArgumentException("error.profile.contact-phone.invalid");
        }

        value = normalized;
    }
}
