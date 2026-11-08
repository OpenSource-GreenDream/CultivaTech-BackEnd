package greendream.cultivatech.platform.iam.infrastructure.security;

import greendream.cultivatech.platform.iam.domain.model.valueobjects.PasswordHash;
import greendream.cultivatech.platform.iam.domain.ports.PasswordHashing;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * BCrypt implementation of the {@link PasswordHashing} domain port.
 */
@Component
public class BCryptPasswordHashing implements PasswordHashing {

    private final BCryptPasswordEncoder encoder;

    public BCryptPasswordHashing() {
        this.encoder = new BCryptPasswordEncoder();
    }

    @Override
    public PasswordHash hash(String plainPassword) {
        if (plainPassword == null || plainPassword.isBlank()) {
            throw new IllegalArgumentException("error.user.password.required");
        }
        return new PasswordHash(encoder.encode(plainPassword));
    }

    @Override
    public boolean matches(String plainPassword, PasswordHash passwordHash) {
        if (plainPassword == null || passwordHash == null) {
            return false;
        }
        return encoder.matches(plainPassword, passwordHash.value());
    }
}
