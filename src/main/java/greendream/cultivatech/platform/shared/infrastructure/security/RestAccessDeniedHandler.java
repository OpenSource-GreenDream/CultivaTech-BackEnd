package greendream.cultivatech.platform.shared.infrastructure.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Returns a consistent 403 problem response when an authenticated principal does
 * not have enough privileges for the requested route.
 */
@Component
public class RestAccessDeniedHandler implements AccessDeniedHandler {

    private static final String MESSAGE_KEY = "error.authentication.forbidden";

    private final MessageSource messageSource;

    public RestAccessDeniedHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {
        String message = messageSource.getMessage(MESSAGE_KEY, null, MESSAGE_KEY, request.getLocale());
        SecurityProblemResponse.write(response, HttpStatus.FORBIDDEN, message);
    }
}
