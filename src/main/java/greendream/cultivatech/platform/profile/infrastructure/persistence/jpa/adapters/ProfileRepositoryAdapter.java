package greendream.cultivatech.platform.profile.infrastructure.persistence.jpa.adapters;

import greendream.cultivatech.platform.profile.domain.model.aggregates.Profile;
import greendream.cultivatech.platform.profile.domain.model.valueobjects.ProfileId;
import greendream.cultivatech.platform.profile.domain.model.valueobjects.UserId;
import greendream.cultivatech.platform.profile.domain.repositories.ProfileRepository;
import greendream.cultivatech.platform.profile.infrastructure.persistence.jpa.entities.ProfilePersistenceEntity;
import greendream.cultivatech.platform.profile.infrastructure.persistence.jpa.mappers.ProfileMapper;
import greendream.cultivatech.platform.profile.infrastructure.persistence.jpa.repositories.ProfileJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * JPA adapter implementing the ProfileRepository domain port.
 */
@Repository
public class ProfileRepositoryAdapter implements ProfileRepository {

    private final ProfileJpaRepository profileJpaRepository;
    private final ProfileMapper mapper;

    public ProfileRepositoryAdapter(ProfileJpaRepository profileJpaRepository, ProfileMapper mapper) {
        this.profileJpaRepository = profileJpaRepository;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public Profile save(Profile profile) {
        ProfilePersistenceEntity entity = profile.id()
                .map(this::findEntity)
                .orElseGet(() -> mapper.toNewEntity(profile));

        if (profile.id().isPresent()) {
            mapper.updateEntity(entity, profile);
        }

        return mapper.toDomain(profileJpaRepository.save(entity));
    }

    @Override
    public Optional<Profile> findById(ProfileId id) {
        return profileJpaRepository.findById(id.value())
                .map(mapper::toDomain);
    }

    @Override
    public Optional<Profile> findByUserId(UserId userId) {
        return profileJpaRepository.findByUserId(userId.value())
                .map(mapper::toDomain);
    }

    @Override
    public boolean existsByUserId(UserId userId) {
        return profileJpaRepository.existsByUserId(userId.value());
    }

    private ProfilePersistenceEntity findEntity(ProfileId id) {
        return profileJpaRepository.findById(id.value())
                .orElseThrow(() -> new IllegalArgumentException("error.profile.not-found"));
    }
}
