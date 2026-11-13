package greendream.cultivatech.platform.shared.infrastructure.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Issues and validates stateless HMAC-signed JWTs.
 *
 * <p>The signing secret is never hard-coded: it is resolved from the
 * {@code security.jwt.secret} property, which is expected to be supplied through
 * the {@code JWT_SECRET} environment variable.</p>
 */
@Service
public class JwtService {

    private static final String EMAIL_CLAIM = "email";
    private static final String ROLES_CLAIM = "roles";
    private static final int MIN_SECRET_BYTES = 32;

    private final SecretKey signingKey;
    private final long expirationSeconds;
    private final String issuer;

    public JwtService(@Value("${security.jwt.secret:}") String secret,
                      @Value("${security.jwt.expiration-seconds:3600}") long expirationSeconds,
                      @Value("${security.jwt.issuer:cultivatech-platform}") String issuer) {
        if (secret == null || secret.isBlank()) {
            throw new IllegalStateException(
                    "security.jwt.secret is not configured; set the JWT_SECRET environment variable"
            );
        }

        byte[] secretBytes = secret.getBytes(StandardCharsets.UTF_8);
        if (secretBytes.length < MIN_SECRET_BYTES) {
            throw new IllegalStateException(
                    "security.jwt.secret must have at least 256 bits (32 bytes) for HMAC-SHA signing"
            );
        }
        if (expirationSeconds <= 0) {
            throw new IllegalStateException("security.jwt.expiration-seconds must be greater than zero");
        }
        if (issuer == null || issuer.isBlank()) {
            throw new IllegalStateException("security.jwt.issuer must not be blank");
        }

        this.signingKey = Keys.hmacShaKeyFor(secretBytes);
        this.expirationSeconds = expirationSeconds;
        this.issuer = issuer;
    }

    /**
     * Issues a token for the given identity and roles.
     *
     * @param userId IAM user identity, used as the {@code sub} claim
     * @param email  user email address
     * @param roles  assigned role names
     * @return the compact token and its lifetime in seconds
     */
    public IssuedToken issue(Long userId, String email, Set<String> roles) {
        Objects.requireNonNull(userId, "userId must not be null");
        Objects.requireNonNull(email, "email must not be null");
        Objects.requireNonNull(roles, "roles must not be null");

        Instant issuedAt = Instant.now();
        Instant expiresAt = issuedAt.plusSeconds(expirationSeconds);
        String token = Jwts.builder()
                .subject(String.valueOf(userId))
                .claim(EMAIL_CLAIM, email)
                .claim(ROLES_CLAIM, List.copyOf(roles))
                .issuedAt(Date.from(issuedAt))
                .expiration(Date.from(expiresAt))
                .issuer(issuer)
                .signWith(signingKey)
                .compact();

        return new IssuedToken(token, expirationSeconds);
    }

    /**
     * Validates a compact token and extracts its principal.
     *
     * @param token compact JWT
     * @return the authenticated identity, or empty when the token is missing, malformed,
     * expired, signed with another key, or issued by another issuer
     */
    public Optional<AuthenticatedUser> validate(String token) {
        if (token == null || token.isBlank()) {
            return Optional.empty();
        }

        try {
            Claims claims = Jwts.parser()
                    .verifyWith(signingKey)
                    .requireIssuer(issuer)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            Long userId = Long.valueOf(claims.getSubject());
            String email = claims.get(EMAIL_CLAIM, String.class);
            return Optional.of(new AuthenticatedUser(userId, email, extractRoles(claims)));
        } catch (JwtException | IllegalArgumentException exception) {
            return Optional.empty();
        }
    }

    public long expirationSeconds() {
        return expirationSeconds;
    }

    private Set<String> extractRoles(Claims claims) {
        Object rawRoles = claims.get(ROLES_CLAIM);
        if (!(rawRoles instanceof Collection<?> roleValues)) {
            return Set.of();
        }
        return roleValues.stream()
                .filter(Objects::nonNull)
                .map(Object::toString)
                .collect(Collectors.toUnmodifiableSet());
    }

    /**
     * Result of issuing a token.
     *
     * @param accessToken     compact JWT
     * @param expiresInSeconds token lifetime in seconds
     */
    public record IssuedToken(String accessToken, long expiresInSeconds) {
    }
}
