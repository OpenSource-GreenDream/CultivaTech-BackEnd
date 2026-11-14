package greendream.cultivatech.platform.monitoring.infrastructure.persistence.jpa.repositories;

import greendream.cultivatech.platform.monitoring.infrastructure.persistence.jpa.entities.DevicePersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data repository for the devices table.
 */
public interface DeviceJpaRepository extends JpaRepository<DevicePersistenceEntity, Long> {

    Optional<DevicePersistenceEntity> findByMacAddress(String macAddress);

    boolean existsByMacAddress(String macAddress);

    List<DevicePersistenceEntity> findByFieldId(Long fieldId);
}
