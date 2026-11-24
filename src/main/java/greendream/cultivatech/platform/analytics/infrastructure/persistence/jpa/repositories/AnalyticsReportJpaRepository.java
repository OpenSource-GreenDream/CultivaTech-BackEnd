package greendream.cultivatech.platform.analytics.infrastructure.persistence.jpa.repositories;

import greendream.cultivatech.platform.analytics.infrastructure.persistence.jpa.entities.AnalyticsReportEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AnalyticsReportJpaRepository extends JpaRepository<AnalyticsReportEntity,Long> {
    List<AnalyticsReportEntity> findAllByDeviceIdOrderByGeneratedAtDesc(Long deviceId);
}
