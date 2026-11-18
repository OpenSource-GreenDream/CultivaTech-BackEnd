package greendream.cultivatech.platform.shared.domain.exceptions;

/**
 * Unchecked exception for failed authentication attempts.
 */
public class UnauthorizedException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public UnauthorizedException(String messageKey) {
        super(messageKey);
    }
}
