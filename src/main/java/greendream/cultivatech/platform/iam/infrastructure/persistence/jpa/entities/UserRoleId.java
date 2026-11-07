package greendream.cultivatech.platform.iam.infrastructure.persistence.jpa.entities;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

/**
 * Composite identifier of {@link UserRolePersistenceEntity}.
 */
@Setter
@Getter
public class UserRoleId implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long userId;
    private Long roleId;

    public UserRoleId() {
    }

    public UserRoleId(Long userId, Long roleId) {
        this.userId = userId;
        this.roleId = roleId;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof UserRoleId that)) {
            return false;
        }
        return Objects.equals(userId, that.userId) && Objects.equals(roleId, that.roleId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, roleId);
    }
}
