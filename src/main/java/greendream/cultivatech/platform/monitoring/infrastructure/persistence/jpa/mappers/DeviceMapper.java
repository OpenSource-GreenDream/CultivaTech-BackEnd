package greendream.cultivatech.platform.monitoring.infrastructure.persistence.jpa.mappers;

import greendream.cultivatech.platform.monitoring.domain.model.aggregates.Device;
import greendream.cultivatech.platform.monitoring.domain.model.valueobjects.DeviceId;
import greendream.cultivatech.platform.monitoring.domain.model.valueobjects.DeviceStatus;
import greendream.cultivatech.platform.monitoring.domain.model.valueobjects.FieldId;
import greendream.cultivatech.platform.monitoring.domain.model.valueobjects.MacAddress;
import greendream.cultivatech.platform.monitoring.infrastructure.persistence.jpa.entities.DevicePersistenceEntity;
import org.springframework.stereotype.Component;

/**
 * Maps between the Device aggregate and its persistence representation.
 */
@Component
public class DeviceMapper {

    public DevicePersistenceEntity toNewEntity(Device device) {
        return new DevicePersistenceEntity(
                device.fieldId().value(),
                device.macAddress().value(),
                device.status().value(),
                device.lastSync()
        );
    }

    public void updateEntity(DevicePersistenceEntity entity, Device device) {
        entity.setStatus(device.status().value());
        entity.setLastSync(device.lastSync());
    }

    public Device toDomain(DevicePersistenceEntity entity) {
        return Device.reconstitute(
                new DeviceId(entity.getId()),
                new FieldId(entity.getFieldId()),
                new MacAddress(entity.getMacAddress()),
                new DeviceStatus(entity.getStatus()),
                entity.getLastSync()
        );
    }
}
