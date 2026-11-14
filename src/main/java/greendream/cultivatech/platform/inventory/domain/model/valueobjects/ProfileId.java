package greendream.cultivatech.platform.inventory.domain.model.valueobjects;

/**
 * Reference to the profile that owns a stock row.
 */
public record ProfileId(Long value) {

    public ProfileId {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("error.inventory.profile-id.invalid");
        }
    }
}
