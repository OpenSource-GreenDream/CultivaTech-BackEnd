package greendream.cultivatech.platform.monitoring.infrastructure.persistence.jpa.repositories;

import greendream.cultivatech.platform.monitoring.infrastructure.persistence.jpa.entities.SensorReadingPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Spring Data repository for the sensor_readings table.
 */
public interface SensorReadingJpaRepository extends JpaRepository<SensorReadingPersistenceEntity, Long> {

    List<SensorReadingPersistenceEntity> findAllByDeviceIdOrderByRecordedAtDesc(Long deviceId);

    List<SensorReadingPersistenceEntity> findAllByOrderByRecordedAtDesc();

    boolean existsByDeviceId(Long deviceId);
}
