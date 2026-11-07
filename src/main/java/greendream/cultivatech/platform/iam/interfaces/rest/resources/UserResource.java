package greendream.cultivatech.platform.iam.interfaces.rest.resources;

import java.util.Set;

/**
 * HTTP response for a registered user.
 */
public record UserResource(
        Long id,
        String email,
        Set<String> roles,
        TemporaryProfileResource profile
) {
}
