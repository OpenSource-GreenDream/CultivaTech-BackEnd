package greendream.cultivatech.platform.iam.domain.model.valueobjects;

import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Name of a role, normalized to upper case.
 */
public record RoleName(String value) {

    private static final Pattern NAME_PATTERN = Pattern.compile("^[A-Z][A-Z0-9_]{1,49}$");

    public RoleName {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("error.role.name.required");
        }

        String normalized = value.trim().toUpperCase(Locale.ROOT);
        if (!NAME_PATTERN.matcher(normalized).matches()) {
            throw new IllegalArgumentException("error.role.name.invalid");
        }

        value = normalized;
    }
}
