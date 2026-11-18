package greendream.cultivatech.platform.iam.application.results;

/**
 * Access token produced by an {@code AccessTokenIssuer}.
 *
 * @param accessToken      serialized and signed token
 * @param expiresInSeconds remaining lifetime in seconds
 */
public record IssuedAccessToken(
        String accessToken,
        long expiresInSeconds
) {
}
