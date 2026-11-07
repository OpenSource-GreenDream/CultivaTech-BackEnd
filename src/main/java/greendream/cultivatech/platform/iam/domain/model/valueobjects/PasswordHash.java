package greendream.cultivatech.platform.iam.domain.model.valueobjects;

/**
 * Opaque representation of an already hashed password.
 *
 * <p>The domain never stores or exposes raw passwords. Hashing is performed by an
 * infrastructure adapter through the {@code PasswordHashing} port.</p>
 */
public record PasswordHash(String value) {

    private static final int MAX_LENGTH = 255;

    public PasswordHash {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("error.user.password-hash.invalid");
        }

        String normalized = value.trim();
        if (normalized.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("error.user.password-hash.invalid");
        }

        value = normalized;
    }

    @Override
    public String toString() {
        return "PasswordHash[REDACTED]";
    }
}
