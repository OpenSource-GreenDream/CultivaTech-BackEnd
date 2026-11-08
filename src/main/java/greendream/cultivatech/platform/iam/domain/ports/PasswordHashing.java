package greendream.cultivatech.platform.iam.domain.ports;

import greendream.cultivatech.platform.iam.domain.model.valueobjects.PasswordHash;

/**
 * Domain port for password hashing.
 * Implemented in infrastructure (for example with BCrypt). The domain only knows
 * the contract, never the algorithm.
 */
public interface PasswordHashing {

    PasswordHash hash(String plainPassword);

    boolean matches(String plainPassword, PasswordHash passwordHash);
}
