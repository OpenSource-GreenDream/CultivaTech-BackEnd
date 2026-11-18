package greendream.cultivatech.platform.iam.infrastructure.tokens.jwt;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

/**
 * Externalized JWT configuration.
 *
 * <p>The signing secret must be supplied through the {@code JWT_SECRET} environment
 * variable. No insecure default is provided. The value is never logged.</p>
 */
@Validated
@ConfigurationProperties(prefix = "authorization.jwt")
public record JwtProperties(
        @NotBlank String issuer,
        @NotBlank @Size(min = 32, message = "authorization.jwt.secret must contain at least 32 characters") String secret,
        @NotNull @Valid Expiration expiration
) {

    /**
     * @return access token lifetime as a duration
     */
    public Duration accessTokenTtl() {
        return Duration.ofMinutes(expiration.minutes());
    }

    @Override
    public String toString() {
        return "JwtProperties[issuer=" + issuer + ", secret=***, expiration=" + expiration + "]";
    }

    /**
     * Nested binding for {@code authorization.jwt.expiration.minutes}.
     *
     * @param minutes access token lifetime in minutes
     */
    public record Expiration(@Min(1) long minutes) {
    }
}
