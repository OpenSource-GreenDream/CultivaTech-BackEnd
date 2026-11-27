package greendream.cultivatech.platform.iam.application.results;

import java.util.Set;

/**
 * Result of a successful sign-in.
 *
 * @param accessToken signed bearer access token
 * @param tokenType   token type expected by the Authorization header
 * @param expiresIn   access token lifetime in seconds
 * @param userId      persisted IAM user identity
 * @param email       normalized email address of the authenticated user
 * @param roles       persisted role names assigned to the user
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
