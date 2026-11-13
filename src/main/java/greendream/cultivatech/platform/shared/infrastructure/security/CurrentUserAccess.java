package greendream.cultivatech.platform.shared.infrastructure.security;

import greendream.cultivatech.platform.shared.domain.exceptions.UnauthorizedException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Resolves the authenticated principal and enforces use-case-level ownership rules
 * that cannot be expressed as a static route matcher.
 */
@Component
public class CurrentUserAccess {

    private static final String ADMIN_ROLE = "ADMIN";

    public AuthenticatedUser currentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof AuthenticatedUser user)) {
            throw new UnauthorizedException("error.authentication.unauthorized");
        }
        return user;
    }

    public Long currentUserId() {
        return currentUser().userId();
    }

    public boolean isAdmin() {
        return currentUser().hasRole(ADMIN_ROLE);
    }

    /**
     * Allows access when the principal is an administrator or owns the target resource.
     *
     * @param userId identity of the resource owner
     */
    public void requireSelfOrAdmin(Long userId) {
        if (isAdmin()) {
            return;
        }
        if (!currentUser().userId().equals(userId)) {
            throw new AccessDeniedException("error.authentication.forbidden");
        }
    }
}
