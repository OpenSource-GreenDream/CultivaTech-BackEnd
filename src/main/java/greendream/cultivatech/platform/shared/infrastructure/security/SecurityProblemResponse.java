package greendream.cultivatech.platform.shared.infrastructure.security;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Writes Spring-style problem responses from the security filter chain so that
 * authentication and authorization failures share the same shape as the ones
 * produced by {@code GlobalExceptionHandler}.
 */
final class SecurityProblemResponse {

    private SecurityProblemResponse() {
    }

    static void write(HttpServletResponse response, HttpStatus status, String detail) throws IOException {
        String title = status.getReasonPhrase();
        String body = "{\"type\":\"about:blank\",\"title\":\"" + escape(title)
                + "\",\"status\":" + status.value()
                + ",\"detail\":\"" + escape(detail) + "\"}";

        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write(body);
    }

    private static String escape(String value) {
        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"");
    }
}
