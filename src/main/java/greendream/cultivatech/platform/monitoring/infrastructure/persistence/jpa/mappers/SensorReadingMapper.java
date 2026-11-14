package greendream.cultivatech.platform.monitoring.infrastructure.persistence.jpa.mappers;

import greendream.cultivatech.platform.monitoring.domain.model.aggregates.SensorReading;
import greendream.cultivatech.platform.monitoring.domain.model.valueobjects.DeviceId;
import greendream.cultivatech.platform.monitoring.infrastructure.persistence.jpa.entities.SensorReadingPersistenceEntity;
import org.springframework.stereotype.Component;

/**
 * Maps between the SensorReading aggregate and its persistence representation.
 */
@Component
public class SensorReadingMapper {

    public SensorReadingPersistenceEntity toNewEntity(SensorReading reading) {
        return new SensorReadingPersistenceEntity(
                reading.getDeviceId().value(),
                reading.getParameter(),
                reading.getValue(),
                reading.getUnit(),
                reading.getRecordedAt()
        );
    }

    public void updateEntity(SensorReadingPersistenceEntity entity, SensorReading reading) {
        entity.setDeviceId(reading.getDeviceId().value());
        entity.setParameter(reading.getParameter());
        entity.setValue(reading.getValue());
        entity.setUnit(reading.getUnit());
        entity.setRecordedAt(reading.getRecordedAt());
    }

    public SensorReading toDomain(SensorReadingPersistenceEntity entity) {
        return SensorReading.restore(
                entity.getId(),
                new DeviceId(entity.getDeviceId()),
                entity.getParameter(),
                entity.getValue(),
                entity.getUnit(),
                entity.getRecordedAt(),
                entity.getCreatedAt()
        );
    }
}
