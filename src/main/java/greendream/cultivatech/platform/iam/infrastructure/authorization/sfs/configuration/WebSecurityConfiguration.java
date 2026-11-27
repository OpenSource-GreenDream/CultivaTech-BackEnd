package greendream.cultivatech.platform.iam.infrastructure.authorization.sfs.configuration;

import greendream.cultivatech.platform.iam.infrastructure.authorization.sfs.handlers.ForbiddenRequestHandler;
import greendream.cultivatech.platform.iam.infrastructure.authorization.sfs.handlers.UnauthorizedRequestHandler;
import greendream.cultivatech.platform.iam.infrastructure.authorization.sfs.pipeline.BearerAuthorizationRequestFilter;
import greendream.cultivatech.platform.iam.infrastructure.authorization.sfs.services.IamUserDetailsService;
import greendream.cultivatech.platform.iam.infrastructure.tokens.jwt.JwtProperties;
import greendream.cultivatech.platform.iam.infrastructure.tokens.jwt.JwtTokenService;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.Customizer;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

import java.time.Clock;

/**
 * Stateless HTTP security configuration for the Cultivatech API.
 *
 * <p>This is a deliberately partial authorization matrix for the current academic
 * iteration. Only the HTTP GET operations of the IAM user and role administration
 * controllers require {@code ROLE_ADMIN}. Every other endpoint keeps the behaviour it
 * had before this change and therefore remains open. This configuration must not be
 * used as-is in a public production deployment.</p>
 */
@Configuration
@EnableWebSecurity
@EnableConfigurationProperties(JwtProperties.class)
public class WebSecurityConfiguration {

    private static final String ADMIN_ROLE = "ADMIN";

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(List.of(
                "http://localhost:4200"
        ));

        configuration.setAllowedMethods(List.of(
                "GET",
                "POST",
                "PUT",
                "PATCH",
                "DELETE",
                "OPTIONS"
        ));

        configuration.setAllowedHeaders(List.of(
                "Authorization",
                "Content-Type",
                "Accept"
        ));

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    @Bean
    public Clock clock() {
        return Clock.systemUTC();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JwtTokenService jwtTokenService,
            IamUserDetailsService iamUserDetailsService,
            UnauthorizedRequestHandler unauthorizedRequestHandler,
            ForbiddenRequestHandler forbiddenRequestHandler) throws Exception {

        BearerAuthorizationRequestFilter bearerAuthorizationRequestFilter =
                new BearerAuthorizationRequestFilter(jwtTokenService, iamUserDetailsService);

        http
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint(unauthorizedRequestHandler)
                        .accessDeniedHandler(forbiddenRequestHandler))
                .authorizeHttpRequests(authorization -> authorization
                        // Public authentication endpoints.
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/v1/authentication/sign-up",
                                "/api/v1/authentication/sign-in")
                        .permitAll()
                        .requestMatchers(HttpMethod.GET,
                                "/api/v1/roles",
                                "/api/v1/roles/*")
                        .permitAll()
                        // Swagger UI and OpenAPI resources.
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/swagger-resources/**",
                                "/webjars/**")
                        .permitAll()
                        .requestMatchers("/error").permitAll()
                        // Demonstration RBAC: GET-only administration reads.
                        // The single wildcard intentionally matches one path segment only,
                        // so /api/v1/users/{userId}/profile from the Profile context keeps
                        // its previous behaviour.
                        .requestMatchers(HttpMethod.POST, "/api/v1/roles", "/api/v1/roles/*")
                        .hasRole(ADMIN_ROLE)
                        .requestMatchers(HttpMethod.GET, "/api/v1/users", "/api/v1/users/*")
                        .hasRole(ADMIN_ROLE)
                        // All remaining endpoints preserve the previous open behaviour
                        // for this academic iteration.
                        .anyRequest().authenticated())
                .addFilterBefore(bearerAuthorizationRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
