package greendream.cultivatech.platform.iam.infrastructure.persistence.jpa.adapters;

import greendream.cultivatech.platform.iam.domain.model.aggregates.Role;
import greendream.cultivatech.platform.iam.domain.model.valueobjects.RoleId;
import greendream.cultivatech.platform.iam.domain.model.valueobjects.RoleName;
import greendream.cultivatech.platform.iam.domain.repositories.RoleRepository;
import greendream.cultivatech.platform.iam.infrastructure.persistence.jpa.mappers.RoleMapper;
import greendream.cultivatech.platform.iam.infrastructure.persistence.jpa.entities.RolePersistenceEntity;
import greendream.cultivatech.platform.iam.infrastructure.persistence.jpa.repositories.RoleJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * JPA adapter implementing RoleRepository.
 */
@Repository
public class RoleRepositoryAdapter implements RoleRepository {

    private final RoleJpaRepository roleJpaRepository;
    private final RoleMapper mapper;

    public RoleRepositoryAdapter(RoleJpaRepository roleJpaRepository,
                                 RoleMapper mapper) {
        this.roleJpaRepository = roleJpaRepository;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public Role save(Role role) {
        RolePersistenceEntity entity = role.id()
                .map(this::findEntity)
                .orElseGet(() -> mapper.toNewEntity(role));

        if (role.id().isPresent()) {
            mapper.updateEntity(entity, role);
        }

        return mapper.toDomain(roleJpaRepository.save(entity));
    }

    @Override
    @Transactional
    public Role update(Role role) {
        return save(role);
    }

    @Override
    public Optional<Role> findById(RoleId id) {
        return roleJpaRepository.findById(id.value()).map(mapper::toDomain);
    }

    @Override
    public Optional<Role> findByName(RoleName name) {
        return roleJpaRepository.findByName(name.value()).map(mapper::toDomain);
    }

    @Override
    public boolean existsByName(RoleName name) {
        return roleJpaRepository.existsByName(name.value());
    }

    @Override
    public List<Role> findAll() {
        return roleJpaRepository.findAll().stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    @Transactional
    public void deleteById(RoleId id) {
        if (!roleJpaRepository.existsById(id.value())) {
            throw new IllegalArgumentException("error.role.not-found");
        }
        roleJpaRepository.deleteById(id.value());
    }

    @Override
    @Transactional
    public void deleteAll() {
        roleJpaRepository.deleteAll();
    }

    private RolePersistenceEntity findEntity(RoleId id) {
        return roleJpaRepository.findById(id.value())
                .orElseThrow(() -> new IllegalArgumentException("error.role.not-found"));
    }
}
