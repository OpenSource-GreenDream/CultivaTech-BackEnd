package greendream.cultivatech.platform.iam.domain.model.valueobjects;

/**
 * Identity of a role.
 */
public record RoleId(Long value) {

    public RoleId {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("error.role.id.invalid");
        }
    }
}
