package greendream.cultivatech.platform.iam.application.results;

import java.util.Set;

/**
 * Result of a successful sign-in.
 *
 * @param accessToken compact JWT
 * @param tokenType   HTTP authorization scheme, always {@code Bearer}
 * @param expiresIn   token lifetime in seconds
 * @param userId      authenticated IAM user identity
 * @param email       normalized email address
 * @param roles       assigned role names
 */
public record SignInResult(
        String accessToken,
        String tokenType,
        long expiresIn,
        Long userId,
        String email,
        Set<String> roles
) {

    public SignInResult {
        roles = Set.copyOf(roles);
    }
}
