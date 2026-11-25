package greendream.cultivatech.platform.analytics.infrastructure.persistence.jpa.repositories;

import greendream.cultivatech.platform.analytics.infrastructure.persistence.jpa.entities.HarvestProjectionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface HarvestProjectionJpaRepository extends JpaRepository<HarvestProjectionEntity,Long> {
    List<HarvestProjectionEntity> findAllByFieldIdOrderByGeneratedAtDesc(Long fieldId);
}
