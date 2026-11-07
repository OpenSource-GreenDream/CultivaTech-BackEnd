package greendream.cultivatech.platform.iam.domain.model.valueobjects;

/**
 * Identity of a user.
 */
public record UserId(Long value) {

    public UserId {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("error.user.id.invalid");
        }
    }
}
