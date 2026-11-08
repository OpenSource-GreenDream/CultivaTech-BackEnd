package greendream.cultivatech.platform.profile.infrastructure.persistence.jpa.repositories;

import greendream.cultivatech.platform.profile.infrastructure.persistence.jpa.entities.ProfilePersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Spring Data repository for the profiles table.
 */
public interface ProfileJpaRepository extends JpaRepository<ProfilePersistenceEntity, Long> {

    Optional<ProfilePersistenceEntity> findByUserId(Long userId);

    boolean existsByUserId(Long userId);
}
