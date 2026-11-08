package greendream.cultivatech.platform.profile.domain.model.valueobjects;

/**
 * Reference to the user that owns a profile.
 *
 * <p>Profile owns this reference locally to keep the domain isolated from the IAM
 * bounded context. Translation from the IAM user identity happens in the application
 * layer.</p>
 */
public record UserId(Long value) {

    public UserId {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("error.profile.user-id.invalid");
        }
    }
}
