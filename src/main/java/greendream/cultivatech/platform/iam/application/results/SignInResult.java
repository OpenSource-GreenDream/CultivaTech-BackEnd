package greendream.cultivatech.platform.iam.application.results;

/**
 * Result of a successful sign-in.
 *
 * @param accessToken signed bearer access token
 * @param tokenType   token type expected by the Authorization header
 * @param expiresIn   access token lifetime in seconds
 */
public record SignInResult(
        String accessToken,
        String tokenType,
        long expiresIn
) {
}
