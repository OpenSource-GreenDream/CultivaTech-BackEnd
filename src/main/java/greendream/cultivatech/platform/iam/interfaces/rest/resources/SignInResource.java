package greendream.cultivatech.platform.iam.interfaces.rest.resources;

import java.util.Set;

/**
 * HTTP response with the issued access token.
 */
public record SignInResource(
        String accessToken,
        String tokenType,
        long expiresIn,
        Long userId,
        String email,
        Set<String> roles
) {
}
