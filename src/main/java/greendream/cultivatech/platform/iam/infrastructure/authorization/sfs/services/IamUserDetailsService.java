package greendream.cultivatech.platform.iam.infrastructure.authorization.sfs.services;

import greendream.cultivatech.platform.iam.domain.model.aggregates.Role;
import greendream.cultivatech.platform.iam.domain.model.aggregates.User;
import greendream.cultivatech.platform.iam.domain.model.valueobjects.RoleName;
import greendream.cultivatech.platform.iam.domain.model.valueobjects.UserId;
import greendream.cultivatech.platform.iam.domain.repositories.RoleRepository;
import greendream.cultivatech.platform.iam.domain.repositories.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Loads the IAM user identified by the JWT subject and resolves its current roles.
 *
 * <p>Roles are deliberately queried on every request so that role changes take effect
 * immediately without issuing a new token.</p>
 */
@Service
public class IamUserDetailsService implements UserDetailsService {

    private static final String ROLE_PREFIX = "ROLE_";

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public IamUserDetailsService(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Long userId = parseUserId(username);
        User user = userRepository.findById(new UserId(userId))
                .orElseThrow(() -> new UsernameNotFoundException("error.authentication.unauthorized"));

        if (!user.isActive()) {
            throw new UsernameNotFoundException("error.authentication.unauthorized");
        }

        Set<GrantedAuthority> authorities = resolveAuthorities(user);
        return new IamUserPrincipal(userId, user.email().value(), user.isActive(), authorities);
    }

    private Long parseUserId(String username) {
        if (username == null || username.isBlank()) {
            throw new UsernameNotFoundException("error.authentication.unauthorized");
        }
        try {
            long userId = Long.parseLong(username);
            if (userId <= 0) {
                throw new UsernameNotFoundException("error.authentication.unauthorized");
            }
            return userId;
        } catch (NumberFormatException exception) {
            throw new UsernameNotFoundException("error.authentication.unauthorized", exception);
        }
    }

    private Set<GrantedAuthority> resolveAuthorities(User user) {
        return roleRepository.findAllById(user.roleIds()).stream()
                .map(Role::name)
                .map(RoleName::value)
                .map(this::toAuthority)
                .collect(Collectors.toUnmodifiableSet());
    }

    private GrantedAuthority toAuthority(String roleName) {
        String authority = roleName.startsWith(ROLE_PREFIX) ? roleName : ROLE_PREFIX + roleName;
        return new SimpleGrantedAuthority(authority);
    }
}
