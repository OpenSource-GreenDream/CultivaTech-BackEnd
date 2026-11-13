package greendream.cultivatech.platform.profile.domain.model.valueobjects;

/**
 * Soil type of a field.
 *
 * <p>The value is kept as free text because the database diagram models
 * {@code fields.soil_type} as {@code VARCHAR(50)} and no controlled vocabulary has
 * been defined for the domain. The value is trimmed and its length is bounded to the
 * column size.</p>
 */
public record SoilType(String value) {

    private static final int MAX_LENGTH = 50;

    public SoilType {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("error.field.soil-type.required");
        }

        String normalized = value.trim();
        if (normalized.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("error.field.soil-type.too-long");
        }

        value = normalized;
    }
}
