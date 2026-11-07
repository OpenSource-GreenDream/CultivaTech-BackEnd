package greendream.cultivatech.platform.iam.infrastructure.persistence.jpa.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Date;

/**
 * Persistence representation of the {@code user_roles} join table.
 *
 * <p>The table has no surrogate key: its identity is the pair
 * {@code (user_id, role_id)}.</p>
 */
@Getter
@Entity
@Table(name = "user_roles")
@IdClass(UserRoleId.class)
@EntityListeners(AuditingEntityListener.class)
public class UserRolePersistenceEntity {

    @Id
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Id
    @Column(name = "role_id", nullable = false)
    private Long roleId;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Date createdAt;

    protected UserRolePersistenceEntity() {
    }

    public UserRolePersistenceEntity(Long userId, Long roleId) {
        this.userId = userId;
        this.roleId = roleId;
    }

}
