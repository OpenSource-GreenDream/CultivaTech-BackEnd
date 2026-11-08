package greendream.cultivatech.platform.iam.infrastructure.persistence.jpa.mappers;

import greendream.cultivatech.platform.iam.domain.model.aggregates.Role;
import greendream.cultivatech.platform.iam.domain.model.valueobjects.RoleId;
import greendream.cultivatech.platform.iam.domain.model.valueobjects.RoleName;
import greendream.cultivatech.platform.iam.infrastructure.persistence.jpa.entities.RolePersistenceEntity;
import org.springframework.stereotype.Component;

/**
 * Maps between the Role aggregate and its persistence representation.
 */
@Component
public class RoleMapper {

    public RolePersistenceEntity toNewEntity(Role role) {
        return new RolePersistenceEntity(
                role.name().value(),
                role.description()
        );
    }

    public void updateEntity(RolePersistenceEntity entity, Role role) {
        entity.setName(role.name().value());
        entity.setDescription(role.description());
    }

    public Role toDomain(RolePersistenceEntity entity) {
        return Role.reconstitute(
                new RoleId(entity.getId()),
                new RoleName(entity.getName()),
                entity.getDescription()
        );
    }
}
