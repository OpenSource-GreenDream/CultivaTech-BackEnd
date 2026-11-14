package greendream.cultivatech.platform.monitoring.domain.repositories;

import greendream.cultivatech.platform.monitoring.domain.model.aggregates.SensorReading;
import greendream.cultivatech.platform.monitoring.domain.model.valueobjects.DeviceId;
import greendream.cultivatech.platform.shared.domain.repositories.ICrudService;

import java.util.List;

/**
 * Domain port for SensorReading persistence.
 * Reuses the shared CRUD contract.
 */
public interface SensorReadingRepository extends ICrudService<SensorReading, Long> {

    List<SensorReading> findAllByDeviceId(DeviceId deviceId);
}
