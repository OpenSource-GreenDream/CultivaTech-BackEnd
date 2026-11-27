package greendream.cultivatech.platform.iam.interfaces.rest.resources;

import java.util.Set;

/**
 * HTTP response for a successful sign-in.
 *
 * @param accessToken signed bearer access token
 * @param tokenType   token type, always {@code Bearer}
 * @param expiresIn   access token lifetime in seconds
 * @param user        authenticated user identity and persisted roles
 */
public record SignInResponse(
        String accessToken,
        String tokenType,
        long expiresIn,
        UserSummaryResource user
) {
}
