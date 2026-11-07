package greendream.cultivatech.platform.iam.infrastructure.persistence.jpa.entities;

import greendream.cultivatech.platform.shared.infrastructure.persistence.jpa.AuditableAbstractPersistenceEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * Persistence representation of the {@code users} table.
 */
@Setter
@Getter
@Entity
@Table(name = "users")
public class UserPersistenceEntity extends AuditableAbstractPersistenceEntity {

    @Column(name = "email_address", nullable = false, unique = true, length = 255)
    private String emailAddress;

    @Column(name = "password_hash", nullable = false, columnDefinition = "TEXT")
    private String passwordHash;

    protected UserPersistenceEntity() {
    }

    public UserPersistenceEntity(String emailAddress, String passwordHash) {
        this.emailAddress = emailAddress;
        this.passwordHash = passwordHash;
    }

}
