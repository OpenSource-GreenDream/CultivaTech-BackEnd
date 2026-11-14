package greendream.cultivatech.platform.monitoring.domain.repositories;

import greendream.cultivatech.platform.monitoring.domain.model.aggregates.SensorReading;
import greendream.cultivatech.platform.monitoring.domain.model.valueobjects.DeviceId;
import greendream.cultivatech.platform.shared.domain.repositories.ICrudService;

import java.util.List;

/**
 * Persistence contract for the sensor reading aggregate.
 */
public interface SensorReadingRepository extends ICrudService<SensorReading, Long> {

    List<SensorReading> findAllByDeviceId(DeviceId deviceId);
}
