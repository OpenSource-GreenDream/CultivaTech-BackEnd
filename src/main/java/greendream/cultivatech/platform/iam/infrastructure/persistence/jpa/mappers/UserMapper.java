package greendream.cultivatech.platform.iam.infrastructure.persistence.jpa.mappers;

import greendream.cultivatech.platform.iam.domain.model.aggregates.User;
import greendream.cultivatech.platform.iam.domain.model.valueobjects.EmailAddress;
import greendream.cultivatech.platform.iam.domain.model.valueobjects.PasswordHash;
import greendream.cultivatech.platform.iam.domain.model.valueobjects.RoleId;
import greendream.cultivatech.platform.iam.domain.model.valueobjects.UserId;
import greendream.cultivatech.platform.iam.infrastructure.persistence.jpa.entities.UserPersistenceEntity;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * Maps between the User aggregate and its persistence representation.
 */
@Component
public class UserMapper {

    public UserPersistenceEntity toNewEntity(User user) {
        return new UserPersistenceEntity(
                user.email().value(),
                user.passwordHash().value()
        );
    }

    public void updateEntity(UserPersistenceEntity entity, User user) {
        entity.setEmailAddress(user.email().value());
        entity.setPasswordHash(user.passwordHash().value());
    }

    public User toDomain(UserPersistenceEntity entity, Set<RoleId> roleIds) {
        return User.reconstitute(
                new UserId(entity.getId()),
                new EmailAddress(entity.getEmailAddress()),
                new PasswordHash(entity.getPasswordHash()),
                roleIds,
                true
        );
    }
}
