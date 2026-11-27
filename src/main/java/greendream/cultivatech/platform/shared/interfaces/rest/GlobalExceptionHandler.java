package greendream.cultivatech.platform.shared.interfaces.rest;

import greendream.cultivatech.platform.shared.domain.exceptions.ConflictException;
import greendream.cultivatech.platform.shared.domain.exceptions.ForbiddenException;
import greendream.cultivatech.platform.shared.domain.exceptions.InvalidSensorParameterException;
import greendream.cultivatech.platform.shared.domain.exceptions.InvalidSensorTimestampException;
import greendream.cultivatech.platform.shared.domain.exceptions.InvalidSensorUnitException;
import greendream.cultivatech.platform.shared.domain.exceptions.InvalidSensorValueException;
import greendream.cultivatech.platform.shared.domain.exceptions.NotFoundException;
import greendream.cultivatech.platform.shared.domain.exceptions.RequiredSensorPropertyException;
import greendream.cultivatech.platform.shared.domain.exceptions.UnauthorizedException;
import greendream.cultivatech.platform.monitoring.domain.model.enums.SensorParameter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import tools.jackson.databind.exc.InvalidFormatException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.ErrorResponse;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Interface layer exception handler translating domain and validation errors
 * to HTTP problem responses. Handles:
 * - {@link IllegalArgumentException}: domain invariant violations and input validation
 * - {@link MethodArgumentNotValidException}: Resource validation failures from
 * Jakarta Bean Validation
 * Localizes error messages per request locale, forming the error translation boundary
 * between domain logic and HTTP clients.
 *
 * @since 1.0
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    private final MessageSource messageSource;

    public GlobalExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    /**
     * Handles MethodArgumentNotValidException.
     *
     * @param exception The {@link MethodArgumentNotValidException} exception to handle
     * @param locale    The {@link Locale} locale to use for error messages
     * @return The {@link ErrorResponse} error response
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    Object handleException(MethodArgumentNotValidException exception, Locale locale) {
        for (FieldError fieldError : exception.getFieldErrors()) {
            String field = fieldError.getField();
            if ("parameter".equals(field)) {
                Map<String, Object> body = new HashMap<>();
                body.put("status", 400);
                body.put("error", "REQUIRED_SENSOR_PARAMETER");
                body.put("message", "Required property 'parameter' is missing.");
                body.put("field", "parameter");
                return ResponseEntity.badRequest().body(body);
            }
            if ("unit".equals(field)) {
                Map<String, Object> body = new HashMap<>();
                body.put("status", 400);
                body.put("error", "REQUIRED_SENSOR_UNIT");
                body.put("message", "Required property 'unit' is missing.");
                body.put("field", "unit");
                return ResponseEntity.badRequest().body(body);
            }
            if ("value".equals(field)) {
                Map<String, Object> body = new HashMap<>();
                body.put("status", 400);
                body.put("error", "REQUIRED_SENSOR_VALUE");
                body.put("message", "Required property 'value' is missing.");
                body.put("field", "value");
                return ResponseEntity.badRequest().body(body);
            }
            if ("recordedAt".equals(field)) {
                Map<String, Object> body = new HashMap<>();
                body.put("status", 400);
                body.put("error", "INVALID_SENSOR_TIMESTAMP");
                body.put("message", "Required property 'recordedAt' is missing.");
                body.put("field", "recordedAt");
                return ResponseEntity.badRequest().body(body);
            }
        }
        String prefix = messageSource.getMessage("errors.found", null, "Validation failed", locale);
        String fields = exception.getFieldErrors().stream()
                .map(fieldError -> messageSource.getMessage(fieldError, locale))
                .collect(Collectors.joining(", "));
        log.warn("Validation failed: {}", fields);
        return ErrorResponse.create(
                exception,
                HttpStatusCode.valueOf(HttpStatus.BAD_REQUEST.value()),
                prefix + " " + fields
        );
    }

    /**
     * Handles IllegalArgumentException.
     *
     * @param exception The {@link IllegalArgumentException} exception to handle
     * @param locale    The {@link Locale} locale to use for error messages
     * @return The {@link ErrorResponse} error response
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ErrorResponse handleException(IllegalArgumentException exception, Locale locale) {
        String messageKey = exception.getMessage() != null ? exception.getMessage() : "errors.found";
        String message = Objects.requireNonNullElse(messageSource.getMessage(messageKey, null, messageKey, locale), messageKey);
        log.warn("Illegal argument: {}", message);
        return ErrorResponse.create(exception, HttpStatusCode.valueOf(HttpStatus.BAD_REQUEST.value()), message);
    }

    /**
     * Handles {@link ConflictException}.
     *
     * @param exception The {@link ConflictException} exception to handle
     * @param locale    The {@link Locale} locale to use for error messages
     * @return The {@link ErrorResponse} error response
     */
    @ExceptionHandler(ConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    ErrorResponse handleException(ConflictException exception, Locale locale) {
        String messageKey = exception.getMessage() != null ? exception.getMessage() : "errors.conflict";
        String message = Objects.requireNonNullElse(
                messageSource.getMessage(messageKey, null, messageKey, locale),
                messageKey
        );
        log.warn("Conflict: {}", message);
        return ErrorResponse.create(
                exception,
                HttpStatusCode.valueOf(HttpStatus.CONFLICT.value()),
                message
        );
    }

    /**
     * Handles {@link NotFoundException}.
     *
     * @param exception The {@link NotFoundException} exception to handle
     * @param locale    The {@link Locale} locale to use for error messages
     * @return The {@link ErrorResponse} error response
     */
    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    ErrorResponse handleException(NotFoundException exception, Locale locale) {
        String messageKey = exception.getMessage() != null ? exception.getMessage() : "errors.not-found";
        String message = Objects.requireNonNullElse(
                messageSource.getMessage(messageKey, null, messageKey, locale),
                messageKey
        );
        log.warn("Not found: {}", message);
        return ErrorResponse.create(
                exception,
                HttpStatusCode.valueOf(HttpStatus.NOT_FOUND.value()),
                message
        );
    }

    /**
     * Handles {@link UnauthorizedException} produced by authentication use cases.
     *
     * @param exception The {@link UnauthorizedException} exception to handle
     * @param locale    The {@link Locale} locale to use for error messages
     * @return The {@link ProblemDetail} error response with a Bearer challenge
     */
    @ExceptionHandler(ForbiddenException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    ErrorResponse handleException(ForbiddenException exception, Locale locale) {
        String messageKey = exception.getMessage() != null ? exception.getMessage() : "error.forbidden";
        String message = Objects.requireNonNullElse(
                messageSource.getMessage(messageKey, null, messageKey, locale),
                messageKey
        );
        log.warn("Forbidden: {}", message);
        return ErrorResponse.create(exception, HttpStatusCode.valueOf(HttpStatus.FORBIDDEN.value()), message);
    }

    @ExceptionHandler(UnauthorizedException.class)
    ResponseEntity<ProblemDetail> handleException(UnauthorizedException exception, Locale locale) {
        String messageKey = exception.getMessage() != null
                ? exception.getMessage()
                : "error.authentication.unauthorized";
        String message = Objects.requireNonNullElse(
                messageSource.getMessage(messageKey, null, messageKey, locale),
                messageKey
        );
        log.warn("Unauthorized: {}", message);

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, message);
        problemDetail.setTitle(HttpStatus.UNAUTHORIZED.getReasonPhrase());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .header(HttpHeaders.WWW_AUTHENTICATE, "Bearer")
                .body(problemDetail);
    }

    @ExceptionHandler(InvalidSensorParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ResponseEntity<Map<String, Object>> handleInvalidSensorParameter(InvalidSensorParameterException exception) {
        Map<String, Object> body = new HashMap<>();
        body.put("status", 400);
        body.put("error", "INVALID_SENSOR_PARAMETER");
        body.put("message", "Invalid sensor parameter '" + exception.getRejectedValue() + "'. Only the following parameters are accepted.");
        body.put("field", "parameter");
        body.put("rejectedValue", exception.getRejectedValue());
        body.put("allowedValues", exception.getAllowedValues());
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(InvalidSensorUnitException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ResponseEntity<Map<String, Object>> handleInvalidSensorUnit(InvalidSensorUnitException exception) {
        Map<String, Object> body = new HashMap<>();
        body.put("status", 400);
        body.put("error", "INVALID_SENSOR_UNIT");
        body.put("message", "Invalid unit '" + exception.getRejectedValue() + "' for parameter " + exception.getParameter() + ". Expected unit: '" + exception.getExpectedUnit() + "'.");
        body.put("field", "unit");
        body.put("parameter", exception.getParameter());
        body.put("rejectedValue", exception.getRejectedValue());
        body.put("allowedValues", exception.getAllowedValues());
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(InvalidSensorValueException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ResponseEntity<Map<String, Object>> handleInvalidSensorValue(InvalidSensorValueException exception) {
        Map<String, Object> body = new HashMap<>();
        body.put("status", 400);
        body.put("error", "INVALID_SENSOR_VALUE");
        String range = exception.getMaxValue() == null
                ? "greater than or equal to " + exception.getMinValue()
                : "between " + exception.getMinValue() + " and " + exception.getMaxValue();
        body.put("message", "Invalid value for " + exception.getParameter() + ". Expected a value " + range + ".");
        body.put("field", "value");
        body.put("parameter", exception.getParameter());
        body.put("rejectedValue", exception.getRejectedValue());
        body.put("minValue", exception.getMinValue());
        if (exception.getMaxValue() != null) {
            body.put("maxValue", exception.getMaxValue());
        }
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(RequiredSensorPropertyException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ResponseEntity<Map<String, Object>> handleRequiredSensorProperty(RequiredSensorPropertyException exception) {
        Map<String, Object> body = new HashMap<>();
        body.put("status", 400);
        body.put("error", exception.getErrorCode());
        body.put("message", "Required property '" + exception.getField() + "' is missing.");
        body.put("field", exception.getField());
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(InvalidSensorTimestampException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ResponseEntity<Map<String, Object>> handleInvalidSensorTimestamp(InvalidSensorTimestampException exception) {
        Map<String, Object> body = new HashMap<>();
        body.put("status", 400);
        body.put("error", "INVALID_SENSOR_TIMESTAMP");
        body.put("message", "Invalid sensor timestamp.");
        body.put("field", "recordedAt");
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    ResponseEntity<Map<String, Object>> handleHttpMessageNotReadable(HttpMessageNotReadableException exception) {
        Throwable cause = exception.getCause();
        InvalidFormatException invalidFormatException = null;
        if (cause instanceof InvalidFormatException) {
            invalidFormatException = (InvalidFormatException) cause;
        }

        if (invalidFormatException != null
                && invalidFormatException.getTargetType() != null
                && invalidFormatException.getTargetType().equals(SensorParameter.class)) {
            Object rejectedValue = invalidFormatException.getValue();
            Map<String, Object> body = new HashMap<>();
            body.put("status", 400);
            body.put("error", "INVALID_SENSOR_PARAMETER");
            body.put("message", "Invalid sensor parameter '" + rejectedValue + "'. Only the following parameters are accepted.");
            body.put("field", "parameter");
            body.put("rejectedValue", rejectedValue);
            body.put("allowedValues", List.of(SensorParameter.values()).stream().map(Enum::name).toList());
            return ResponseEntity.badRequest().body(body);
        }
        Map<String, Object> body = new HashMap<>();
        body.put("status", 400);
        body.put("error", "BAD_REQUEST");
        body.put("message", exception.getMessage());
        return ResponseEntity.badRequest().body(body);
    }

}
