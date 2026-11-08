package greendream.cultivatech.platform.iam.infrastructure.persistence.jpa.entities;

import greendream.cultivatech.platform.shared.infrastructure.persistence.jpa.AuditableAbstractPersistenceEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * Persistence representation of the {@code roles} table.
 */
@Setter
@Getter
@Entity
@Table(name = "roles")
public class RolePersistenceEntity extends AuditableAbstractPersistenceEntity {

    @Column(name = "name", nullable = false, unique = true, length = 50)
    private String name;

    @Column(name = "description", length = 255)
    private String description;

    protected RolePersistenceEntity() {
    }

    public RolePersistenceEntity(String name, String description) {
        this.name = name;
        this.description = description;
    }

}
