package greendream.cultivatech.platform.iam.application.ports;

import java.util.Set;

/**
 * Outbound port for issuing access tokens.
 * This keeps the IAM application layer independent of the JWT library and of the
 * shared security infrastructure. The implementation delegates to the shared
 * token service.
 */
public interface TokenIssuer {

    /**
     * Issues an access token for the given identity and roles.
     *
     * @param userId IAM user identity
     * @param email  user email address
     * @param roles  assigned role names
     * @return the token and its lifetime in seconds
     */
    IssuedToken issue(Long userId, String email, Set<String> roles);

    /**
     * Issued token payload.
     *
     * @param accessToken      compact token
     * @param expiresInSeconds token lifetime in seconds
     */
    record IssuedToken(String accessToken, long expiresInSeconds) {
    }
}
