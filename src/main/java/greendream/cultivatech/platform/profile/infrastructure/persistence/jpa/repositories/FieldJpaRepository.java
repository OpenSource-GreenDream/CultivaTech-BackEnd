package greendream.cultivatech.platform.profile.infrastructure.persistence.jpa.repositories;

import greendream.cultivatech.platform.profile.infrastructure.persistence.jpa.entities.FieldPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Spring Data repository for the fields table.
 */
public interface FieldJpaRepository extends JpaRepository<FieldPersistenceEntity, Long> {

    List<FieldPersistenceEntity> findByProfileId(Long profileId);

    boolean existsByProfileId(Long profileId);
}
