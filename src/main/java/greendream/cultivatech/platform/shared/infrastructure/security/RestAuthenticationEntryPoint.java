package greendream.cultivatech.platform.shared.infrastructure.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Returns a consistent 401 problem response when a request reaches a protected
 * route without valid authentication.
 */
@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private static final String MESSAGE_KEY = "error.authentication.unauthorized";

    private final MessageSource messageSource;

    public RestAuthenticationEntryPoint(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        String message = messageSource.getMessage(MESSAGE_KEY, null, MESSAGE_KEY, request.getLocale());
        SecurityProblemResponse.write(response, HttpStatus.UNAUTHORIZED, message);
    }
}
