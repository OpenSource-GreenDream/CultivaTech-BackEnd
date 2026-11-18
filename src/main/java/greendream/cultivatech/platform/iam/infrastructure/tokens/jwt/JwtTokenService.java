package greendream.cultivatech.platform.iam.infrastructure.tokens.jwt;

import greendream.cultivatech.platform.iam.application.ports.AccessTokenIssuer;
import greendream.cultivatech.platform.iam.application.results.IssuedAccessToken;
import greendream.cultivatech.platform.iam.domain.model.valueobjects.UserId;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Objects;

/**
 * JJWT-backed implementation of the {@link AccessTokenIssuer} application port.
 *
 * <p>Tokens are signed with HS256 and contain only the immutable user identifier as
 * {@code sub}, the issuer, the issued-at instant and the expiration instant. Roles are
 * intentionally not included: they are reloaded from the database on every request.</p>
 */
@Component
public class JwtTokenService implements AccessTokenIssuer {

    private static final int MIN_SECRET_BYTES = 32;

    private final SecretKey signingKey;
    private final String issuer;
    private final Duration accessTokenTtl;
    private final Clock clock;

    public JwtTokenService(JwtProperties properties, Clock clock) {
        Objects.requireNonNull(properties, "properties must not be null");
        this.clock = Objects.requireNonNull(clock, "clock must not be null");
        this.issuer = properties.issuer();
        this.accessTokenTtl = properties.accessTokenTtl();

        byte[] secretBytes = Objects.requireNonNullElse(properties.secret(), "")
                .getBytes(StandardCharsets.UTF_8);
        if (secretBytes.length < MIN_SECRET_BYTES) {
            throw new IllegalStateException(
                    "authorization.jwt.secret must be at least 256 bits (32 bytes) long");
        }
        this.signingKey = Keys.hmacShaKeyFor(secretBytes);
    }

    @Override
    public IssuedAccessToken issue(UserId userId) {
        Objects.requireNonNull(userId, "userId must not be null");

        Instant issuedAt = clock.instant();
        Instant expiresAt = issuedAt.plus(accessTokenTtl);

        String token = Jwts.builder()
                .subject(String.valueOf(userId.value()))
                .issuer(issuer)
                .issuedAt(Date.from(issuedAt))
                .expiration(Date.from(expiresAt))
                .signWith(signingKey, Jwts.SIG.HS256)
                .compact();

        return new IssuedAccessToken(token, accessTokenTtl.getSeconds());
    }

    /**
     * Verifies the token signature, issuer and expiration and returns its subject.
     *
     * @param token compact bearer token
     * @return immutable user identifier
     * @throws io.jsonwebtoken.JwtException if the token is malformed, tampered with or expired
     * @throws IllegalArgumentException     if the subject is missing or not a valid user identifier
     */
    public Long extractUserId(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(signingKey)
                .requireIssuer(issuer)
                .clock(() -> Date.from(clock.instant()))
                .build()
                .parseSignedClaims(token)
                .getPayload();

        String subject = claims.getSubject();
        if (subject == null || subject.isBlank()) {
            throw new IllegalArgumentException("JWT subject is missing");
        }

        try {
            long userId = Long.parseLong(subject);
            if (userId <= 0) {
                throw new IllegalArgumentException("JWT subject is not a valid user identifier");
            }
            return userId;
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("JWT subject is not a valid user identifier", exception);
        }
    }
}
