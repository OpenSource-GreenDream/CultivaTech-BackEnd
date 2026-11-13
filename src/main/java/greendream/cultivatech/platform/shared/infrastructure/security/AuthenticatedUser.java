package greendream.cultivatech.platform.shared.infrastructure.security;

import java.util.Objects;
import java.util.Set;

/**
 * Authenticated principal extracted from a validated JWT.
 *
 * @param userId IAM identity of the authenticated user
 * @param email  email address carried by the token
 * @param roles  role names carried by the token
 */
public record AuthenticatedUser(Long userId, String email, Set<String> roles) {

    public AuthenticatedUser {
        Objects.requireNonNull(userId, "userId must not be null");
        roles = Set.copyOf(Objects.requireNonNull(roles, "roles must not be null"));
    }

    public boolean hasRole(String roleName) {
        return roles.contains(roleName);
    }
}
