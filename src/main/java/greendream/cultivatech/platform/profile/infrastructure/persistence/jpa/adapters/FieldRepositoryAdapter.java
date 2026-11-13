package greendream.cultivatech.platform.profile.infrastructure.persistence.jpa.adapters;

import greendream.cultivatech.platform.profile.domain.model.aggregates.Field;
import greendream.cultivatech.platform.profile.domain.model.valueobjects.FieldId;
import greendream.cultivatech.platform.profile.domain.model.valueobjects.ProfileId;
import greendream.cultivatech.platform.profile.domain.repositories.FieldRepository;
import greendream.cultivatech.platform.profile.infrastructure.persistence.jpa.entities.FieldPersistenceEntity;
import greendream.cultivatech.platform.profile.infrastructure.persistence.jpa.mappers.FieldMapper;
import greendream.cultivatech.platform.profile.infrastructure.persistence.jpa.repositories.FieldJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * JPA adapter implementing the FieldRepository domain port.
 */
@Repository
public class FieldRepositoryAdapter implements FieldRepository {

    private final FieldJpaRepository fieldJpaRepository;
    private final FieldMapper mapper;

    public FieldRepositoryAdapter(FieldJpaRepository fieldJpaRepository, FieldMapper mapper) {
        this.fieldJpaRepository = fieldJpaRepository;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public Field save(Field field) {
        FieldPersistenceEntity entity = field.id()
                .map(this::findEntity)
                .orElseGet(() -> mapper.toNewEntity(field));

        if (field.id().isPresent()) {
            mapper.updateEntity(entity, field);
        }

        return mapper.toDomain(fieldJpaRepository.save(entity));
    }

    @Override
    @Transactional
    public Field update(Field field) {
        if (field.id().isEmpty()) {
            throw new IllegalArgumentException("error.field.id.not-assigned");
        }
        return save(field);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Field> findAll() {
        return fieldJpaRepository.findAll().stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Field> findById(FieldId id) {
        return fieldJpaRepository.findById(id.value())
                .map(mapper::toDomain);
    }

    @Override
    @Transactional
    public void deleteById(FieldId id) {
        fieldJpaRepository.delete(findEntity(id));
    }

    @Override
    @Transactional
    public void deleteAll() {
        fieldJpaRepository.deleteAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Field> findByProfileId(ProfileId profileId) {
        return fieldJpaRepository.findByProfileId(profileId.value()).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByProfileId(ProfileId profileId) {
        return fieldJpaRepository.existsByProfileId(profileId.value());
    }

    private FieldPersistenceEntity findEntity(FieldId id) {
        return fieldJpaRepository.findById(id.value())
                .orElseThrow(() -> new IllegalArgumentException("error.field.not-found"));
    }
}
