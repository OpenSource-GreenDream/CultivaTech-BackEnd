package greendream.cultivatech.platform.shared.domain.exceptions;

/**
 * Unchecked exception for resources that cannot be found.
 */
public class NotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public NotFoundException(String messageKey) {
        super(messageKey);
    }
}
