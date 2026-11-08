package greendream.cultivatech.platform.iam.infrastructure.persistence.jpa.adapters;

import greendream.cultivatech.platform.iam.domain.model.aggregates.User;
import greendream.cultivatech.platform.iam.domain.model.valueobjects.EmailAddress;
import greendream.cultivatech.platform.iam.domain.model.valueobjects.RoleId;
import greendream.cultivatech.platform.iam.domain.model.valueobjects.UserId;
import greendream.cultivatech.platform.iam.domain.repositories.UserRepository;
import greendream.cultivatech.platform.iam.infrastructure.persistence.jpa.mappers.UserMapper;
import greendream.cultivatech.platform.iam.infrastructure.persistence.jpa.entities.UserPersistenceEntity;
import greendream.cultivatech.platform.iam.infrastructure.persistence.jpa.entities.UserRolePersistenceEntity;
import greendream.cultivatech.platform.iam.infrastructure.persistence.jpa.repositories.UserJpaRepository;
import greendream.cultivatech.platform.iam.infrastructure.persistence.jpa.repositories.UserRoleJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * JPA adapter implementing UserRepository
 * The user_roles rows are synchronized after the user is persisted,
 * because their composite key needs the generated user ID.
 */
@Repository
public class UserRepositoryAdapter implements UserRepository {

    private final UserJpaRepository userJpaRepository;
    private final UserRoleJpaRepository userRoleJpaRepository;
    private final UserMapper mapper;

    public UserRepositoryAdapter(UserJpaRepository userJpaRepository,
                                 UserRoleJpaRepository userRoleJpaRepository,
                                 UserMapper mapper) {
        this.userJpaRepository = userJpaRepository;
        this.userRoleJpaRepository = userRoleJpaRepository;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public User save(User user) {
        UserPersistenceEntity entity = user.id()
                .map(this::findEntity)
                .orElseGet(() -> mapper.toNewEntity(user));

        if (user.id().isPresent()) {
            mapper.updateEntity(entity, user);
        }

        UserPersistenceEntity saved = userJpaRepository.save(entity);
        Long userId = saved.getId();
        Set<RoleId> roleIds = user.roleIds();

        userRoleJpaRepository.deleteByUserId(userId);
        userRoleJpaRepository.flush();
        if (!roleIds.isEmpty()) {
            List<UserRolePersistenceEntity> assignments = roleIds.stream()
                    .map(roleId -> new UserRolePersistenceEntity(userId, roleId.value()))
                    .toList();
            userRoleJpaRepository.saveAll(assignments);
        }

        return mapper.toDomain(saved, roleIds);
    }

    @Override
    public Optional<User> findById(UserId id) {
        return userJpaRepository.findById(id.value())
                .map(entity -> mapper.toDomain(entity, loadRoleIds(entity.getId())));
    }

    @Override
    public Optional<User> findByEmail(EmailAddress email) {
        return userJpaRepository.findByEmailAddress(email.value())
                .map(entity -> mapper.toDomain(entity, loadRoleIds(entity.getId())));
    }

    @Override
    public boolean existsByEmail(EmailAddress email) {
        return userJpaRepository.existsByEmailAddress(email.value());
    }

    private UserPersistenceEntity findEntity(UserId id) {
        return userJpaRepository.findById(id.value())
                .orElseThrow(() -> new IllegalArgumentException("error.user.not-found"));
    }

    private Set<RoleId> loadRoleIds(Long userId) {
        return userRoleJpaRepository.findByUserId(userId).stream()
                .map(assignment -> new RoleId(assignment.getRoleId()))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }
}
