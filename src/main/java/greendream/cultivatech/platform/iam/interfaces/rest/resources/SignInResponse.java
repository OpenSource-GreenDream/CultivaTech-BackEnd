package greendream.cultivatech.platform.iam.interfaces.rest.resources;

/**
 * HTTP response for a successful sign-in.
 *
 * @param accessToken signed bearer access token
 * @param tokenType   token type, always {@code Bearer}
 * @param expiresIn   access token lifetime in seconds
 */
public record SignInResponse(
        String accessToken,
        String tokenType,
        long expiresIn
) {
}
