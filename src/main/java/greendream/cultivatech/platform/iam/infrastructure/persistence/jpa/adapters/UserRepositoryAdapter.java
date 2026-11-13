package greendream.cultivatech.platform.iam.infrastructure.persistence.jpa.adapters;

import greendream.cultivatech.platform.iam.domain.model.aggregates.User;
import greendream.cultivatech.platform.iam.domain.model.valueobjects.EmailAddress;
import greendream.cultivatech.platform.iam.domain.model.valueobjects.RoleId;
import greendream.cultivatech.platform.iam.domain.model.valueobjects.UserId;
import greendream.cultivatech.platform.iam.domain.repositories.UserRepository;
import greendream.cultivatech.platform.iam.infrastructure.persistence.jpa.entities.RolePersistenceEntity;
import greendream.cultivatech.platform.iam.infrastructure.persistence.jpa.entities.UserPersistenceEntity;
import greendream.cultivatech.platform.iam.infrastructure.persistence.jpa.mappers.UserMapper;
import greendream.cultivatech.platform.iam.infrastructure.persistence.jpa.repositories.RoleJpaRepository;
import greendream.cultivatech.platform.iam.infrastructure.persistence.jpa.repositories.UserJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * JPA adapter implementing UserRepository.
 *
 * <p>Role assignments are persisted through the {@code user_roles} join table
 * mapped with {@code @ManyToMany} on {@link UserPersistenceEntity}.</p>
 */
@Repository
public class UserRepositoryAdapter implements UserRepository {

    private final UserJpaRepository userJpaRepository;
    private final RoleJpaRepository roleJpaRepository;
    private final UserMapper mapper;

    public UserRepositoryAdapter(UserJpaRepository userJpaRepository,
                                 RoleJpaRepository roleJpaRepository,
                                 UserMapper mapper) {
        this.userJpaRepository = userJpaRepository;
        this.roleJpaRepository = roleJpaRepository;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public User save(User user) {
        Set<RolePersistenceEntity> roles = loadRoleEntities(user.roleIds());
        UserPersistenceEntity entity = user.id()
                .map(this::findEntity)
                .orElseGet(() -> mapper.toNewEntity(user, roles));

        if (user.id().isPresent()) {
            mapper.updateEntity(entity, user, roles);
        }

        return mapper.toDomain(userJpaRepository.save(entity));
    }

    @Override
    @Transactional
    public User update(User user) {
        return save(user);
    }

    @Override
    public Optional<User> findById(UserId id) {
        return userJpaRepository.findById(id.value()).map(mapper::toDomain);
    }

    @Override
    public List<User> findAll() {
        return userJpaRepository.findAll().stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Optional<User> findByEmail(EmailAddress email) {
        return userJpaRepository.findByEmailAddress(email.value()).map(mapper::toDomain);
    }

    @Override
    public boolean existsByEmail(EmailAddress email) {
        return userJpaRepository.existsByEmailAddress(email.value());
    }

    @Override
    public boolean existsByRoleId(RoleId roleId) {
        return userJpaRepository.existsByRoles_Id(roleId.value());
    }

    @Override
    @Transactional
    public void deleteById(UserId id) {
        if (!userJpaRepository.existsById(id.value())) {
            throw new IllegalArgumentException("error.user.not-found");
        }
        userJpaRepository.deleteById(id.value());
    }

    @Override
    @Transactional
    public void deleteAll() {
        userJpaRepository.deleteAll();
    }

    private UserPersistenceEntity findEntity(UserId id) {
        return userJpaRepository.findById(id.value())
                .orElseThrow(() -> new IllegalArgumentException("error.user.not-found"));
    }

    private Set<RolePersistenceEntity> loadRoleEntities(Set<RoleId> roleIds) {
        List<Long> ids = roleIds.stream()
                .map(RoleId::value)
                .toList();
        List<RolePersistenceEntity> roles = roleJpaRepository.findAllById(ids);
        if (roles.size() != ids.size()) {
            throw new IllegalArgumentException("error.role.not-found");
        }
        return new HashSet<>(roles);
    }
}
