package greendream.cultivatech.platform.profile.domain.model.valueobjects;

/**
 * Name of the fundo (farm) associated with a profile.
 *
 * <p>The value is trimmed so that accidental surrounding whitespace does not create
 * semantically different profiles.</p>
 */
public record FundoName(String value) {

    private static final int MAX_LENGTH = 120;

    public FundoName {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("error.profile.fundo-name.required");
        }

        String normalized = value.trim();
        if (normalized.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("error.profile.fundo-name.too-long");
        }

        value = normalized;
    }
}
