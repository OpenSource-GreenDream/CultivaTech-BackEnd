package greendream.cultivatech.platform.iam.infrastructure.persistence.jpa.mappers;

import greendream.cultivatech.platform.iam.domain.model.aggregates.User;
import greendream.cultivatech.platform.iam.domain.model.valueobjects.EmailAddress;
import greendream.cultivatech.platform.iam.domain.model.valueobjects.PasswordHash;
import greendream.cultivatech.platform.iam.domain.model.valueobjects.RoleId;
import greendream.cultivatech.platform.iam.domain.model.valueobjects.UserId;
import greendream.cultivatech.platform.iam.infrastructure.persistence.jpa.entities.RolePersistenceEntity;
import greendream.cultivatech.platform.iam.infrastructure.persistence.jpa.entities.UserPersistenceEntity;
import org.springframework.stereotype.Component;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Maps between the User aggregate and its persistence representation.
 */
@Component
public class UserMapper {

    public UserPersistenceEntity toNewEntity(User user, Set<RolePersistenceEntity> roles) {
        UserPersistenceEntity entity = new UserPersistenceEntity(
                user.email().value(),
                user.passwordHash().value()
        );
        entity.setActive(user.isActive());
        entity.setRoles(roles);
        return entity;
    }

    public void updateEntity(UserPersistenceEntity entity, User user, Set<RolePersistenceEntity> roles) {
        entity.setEmailAddress(user.email().value());
        entity.setPasswordHash(user.passwordHash().value());
        entity.setActive(user.isActive());
        entity.setRoles(roles);
    }

    public User toDomain(UserPersistenceEntity entity) {
        return User.reconstitute(
                new UserId(entity.getId()),
                new EmailAddress(entity.getEmailAddress()),
                new PasswordHash(entity.getPasswordHash()),
                toRoleIds(entity.getRoles()),
                entity.isActive()
        );
    }

    private Set<RoleId> toRoleIds(Set<RolePersistenceEntity> roles) {
        if (roles == null) {
            return Set.of();
        }
        return roles.stream()
                .map(role -> new RoleId(role.getId()))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }
}
