package greendream.cultivatech.platform.monitoring.domain.repositories;

import greendream.cultivatech.platform.monitoring.domain.model.aggregates.Device;
import greendream.cultivatech.platform.monitoring.domain.model.valueobjects.DeviceId;
import greendream.cultivatech.platform.monitoring.domain.model.valueobjects.FieldId;
import greendream.cultivatech.platform.monitoring.domain.model.valueobjects.MacAddress;
import greendream.cultivatech.platform.shared.domain.repositories.ICrudService;

import java.util.List;
import java.util.Optional;

/**
 * Persistence contract for the device aggregate.
 */
public interface DeviceRepository extends ICrudService<Device, DeviceId> {

    Optional<Device> findByMacAddress(MacAddress macAddress);

    boolean existsByMacAddress(MacAddress macAddress);

    List<Device> findByFieldId(FieldId fieldId);
}
