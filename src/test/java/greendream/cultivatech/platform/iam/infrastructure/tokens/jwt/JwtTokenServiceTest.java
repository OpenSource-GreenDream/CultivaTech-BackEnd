package greendream.cultivatech.platform.iam.infrastructure.tokens.jwt;

import greendream.cultivatech.platform.iam.application.results.IssuedAccessToken;
import greendream.cultivatech.platform.iam.domain.model.valueobjects.UserId;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JwtTokenServiceTest {

    private static final String SECRET = "unit-test-secret-with-at-least-32-characters";
    private static final String OTHER_SECRET = "another-unit-test-secret-with-32-characters";
    private static final String ISSUER = "cultivatech-platform";
    private static final Instant FIXED_INSTANT = Instant.parse("2026-01-01T10:00:00Z");

    @Test
    void issuesTokenWithExpectedClaims() {
        Clock clock = Clock.fixed(FIXED_INSTANT, ZoneOffset.UTC);
        JwtTokenService service = new JwtTokenService(properties(SECRET, 30), clock);

        IssuedAccessToken issued = service.issue(new UserId(42L));

        Claims claims = Jwts.parser()
                .verifyWith(secretKey(SECRET))
                .clock(() -> Date.from(FIXED_INSTANT))
                .build()
                .parseSignedClaims(issued.accessToken())
                .getPayload();

        assertEquals("42", claims.getSubject());
        assertEquals(ISSUER, claims.getIssuer());
        assertEquals(Date.from(FIXED_INSTANT), claims.getIssuedAt());
        assertEquals(Date.from(FIXED_INSTANT.plus(Duration.ofMinutes(30))), claims.getExpiration());
        assertFalse(claims.containsKey("roles"));
        assertEquals(1800L, issued.expiresInSeconds());
    }

    @Test
    void extractsUserIdFromValidToken() {
        JwtTokenService service = new JwtTokenService(properties(SECRET, 30), Clock.systemUTC());
        IssuedAccessToken issued = service.issue(new UserId(7L));

        assertEquals(7L, service.extractUserId(issued.accessToken()));
    }

    @Test
    void rejectsTamperedSignature() {
        JwtTokenService service = new JwtTokenService(properties(SECRET, 30), Clock.systemUTC());
        String token = service.issue(new UserId(7L)).accessToken();
        String tampered = token.substring(0, token.length() - 1) + (token.endsWith("a") ? "b" : "a");

        assertThrows(JwtException.class, () -> service.extractUserId(tampered));
    }

    @Test
    void rejectsTokenSignedWithAnotherSecret() {
        JwtTokenService issuer = new JwtTokenService(properties(SECRET, 30), Clock.systemUTC());
        JwtTokenService verifier = new JwtTokenService(properties(OTHER_SECRET, 30), Clock.systemUTC());
        String token = issuer.issue(new UserId(7L)).accessToken();

        assertThrows(JwtException.class, () -> verifier.extractUserId(token));
    }

    @Test
    void rejectsExpiredToken() {
        Clock issuingClock = Clock.fixed(FIXED_INSTANT, ZoneOffset.UTC);
        JwtTokenService issuer = new JwtTokenService(properties(SECRET, 30), issuingClock);
        String token = issuer.issue(new UserId(7L)).accessToken();

        Clock laterClock = Clock.fixed(FIXED_INSTANT.plus(Duration.ofMinutes(31)), ZoneOffset.UTC);
        JwtTokenService verifier = new JwtTokenService(properties(SECRET, 30), laterClock);

        assertThrows(JwtException.class, () -> verifier.extractUserId(token));
    }

    @Test
    void rejectsMalformedToken() {
        JwtTokenService service = new JwtTokenService(properties(SECRET, 30), Clock.systemUTC());

        assertThrows(JwtException.class, () -> service.extractUserId("this-is-not-a-jwt"));
    }

    @Test
    void rejectsSecretShorterThan256Bits() {
        assertThrows(IllegalStateException.class,
                () -> new JwtTokenService(properties("too-short-secret", 30), Clock.systemUTC()));
    }

    private JwtProperties properties(String secret, long minutes) {
        return new JwtProperties(ISSUER, secret, new JwtProperties.Expiration(minutes));
    }

    private SecretKey secretKey(String secret) {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
}
