package greendream.cultivatech.platform.iam.infrastructure.authorization.sfs.services;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Authenticated IAM user exposed to Spring Security.
 *
 * <p>The principal holds the immutable user identifier, the identity used by Spring
 * Security (the email address) and the authorities resolved from the current database
 * roles. Password hashes are never exposed.</p>
 */
public final class IamUserPrincipal implements UserDetails {

    @Serial
    private static final long serialVersionUID = 1L;

    private final Long userId;
    private final String email;
    private final boolean active;
    private final Set<GrantedAuthority> authorities;

    public IamUserPrincipal(Long userId,
                            String email,
                            boolean active,
                            Collection<? extends GrantedAuthority> authorities) {
        this.userId = Objects.requireNonNull(userId, "userId must not be null");
        this.email = Objects.requireNonNull(email, "email must not be null");
        this.active = active;
        this.authorities = Collections.unmodifiableSet(
                new LinkedHashSet<>(Objects.requireNonNull(authorities, "authorities must not be null")));
    }

    /**
     * @return immutable IAM user identifier
     */
    public Long userId() {
        return userId;
    }

    /**
     * Alias convenient for framework integrations that expect an {@code id} property.
     *
     * @return immutable IAM user identifier
     */
    public Long getId() {
        return userId;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    /**
     * @return {@code null}; password hashes are never exposed through the principal
     */
    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return active;
    }
}
