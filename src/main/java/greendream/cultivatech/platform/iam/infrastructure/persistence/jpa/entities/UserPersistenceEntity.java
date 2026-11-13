package greendream.cultivatech.platform.iam.infrastructure.persistence.jpa.entities;

import greendream.cultivatech.platform.shared.infrastructure.persistence.jpa.AuditableAbstractPersistenceEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

import java.util.HashSet;
import java.util.Set;

/**
 * Persistence representation of the {@code users} table.
 */
@Entity
@Table(name = "users")
public class UserPersistenceEntity extends AuditableAbstractPersistenceEntity {

    @Column(name = "email_address", nullable = false, unique = true, length = 255)
    private String emailAddress;

    @Column(name = "password_hash", nullable = false, columnDefinition = "TEXT")
    private String passwordHash;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<RolePersistenceEntity> roles = new HashSet<>();

    protected UserPersistenceEntity() {
    }

    public UserPersistenceEntity(String emailAddress, String passwordHash) {
        this.emailAddress = emailAddress;
        this.passwordHash = passwordHash;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public Set<RolePersistenceEntity> getRoles() {
        return roles;
    }

    public void setRoles(Set<RolePersistenceEntity> roles) {
        this.roles = roles;
    }
}
