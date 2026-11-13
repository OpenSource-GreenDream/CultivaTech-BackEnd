package greendream.cultivatech.platform.shared.domain.exceptions;

/**
 * Unchecked exception for operations that require valid authentication
 * credentials but were not given any, or were given invalid ones.
 */
public class UnauthorizedException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public UnauthorizedException(String messageKey) {
        super(messageKey);
    }
}
