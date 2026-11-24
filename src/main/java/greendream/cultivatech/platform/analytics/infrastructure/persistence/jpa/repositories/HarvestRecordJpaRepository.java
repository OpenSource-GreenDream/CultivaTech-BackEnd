package greendream.cultivatech.platform.analytics.infrastructure.persistence.jpa.repositories;

import greendream.cultivatech.platform.analytics.infrastructure.persistence.jpa.entities.HarvestRecordEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface HarvestRecordJpaRepository extends JpaRepository<HarvestRecordEntity,Long> {
    List<HarvestRecordEntity> findAllByFieldIdAndCropTypeIgnoreCaseOrderByHarvestedAtDesc(Long fieldId,String cropType);
}
