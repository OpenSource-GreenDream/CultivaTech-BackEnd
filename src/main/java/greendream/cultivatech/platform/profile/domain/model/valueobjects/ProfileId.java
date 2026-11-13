package greendream.cultivatech.platform.profile.domain.model.valueobjects;

/**
 * Identity of a profile.
 */
public record ProfileId(Long value) {

    public ProfileId {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("error.profile.id.invalid");
        }
    }
}
