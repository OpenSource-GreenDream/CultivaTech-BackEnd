package greendream.cultivatech.platform.iam.infrastructure.security;

import greendream.cultivatech.platform.iam.application.ports.TokenIssuer;
import greendream.cultivatech.platform.shared.infrastructure.security.JwtService;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * Adapter that implements the IAM {@link TokenIssuer} port on top of the shared
 * {@link JwtService}.
 */
@Component
public class JwtTokenIssuer implements TokenIssuer {

    private final JwtService jwtService;

    public JwtTokenIssuer(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public IssuedToken issue(Long userId, String email, Set<String> roles) {
        JwtService.IssuedToken issuedToken = jwtService.issue(userId, email, roles);
        return new IssuedToken(issuedToken.accessToken(), issuedToken.expiresInSeconds());
    }
}
