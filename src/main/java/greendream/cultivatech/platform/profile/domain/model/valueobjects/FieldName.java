package greendream.cultivatech.platform.profile.domain.model.valueobjects;

/**
 * Name of a field.
 *
 * <p>The value is trimmed so that accidental surrounding whitespace does not create
 * semantically different fields. The maximum length matches the {@code fields.name}
 * column of the database diagram.</p>
 */
public record FieldName(String value) {

    private static final int MAX_LENGTH = 100;

    public FieldName {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("error.field.name.required");
        }

        String normalized = value.trim();
        if (normalized.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("error.field.name.too-long");
        }

        value = normalized;
    }
}
