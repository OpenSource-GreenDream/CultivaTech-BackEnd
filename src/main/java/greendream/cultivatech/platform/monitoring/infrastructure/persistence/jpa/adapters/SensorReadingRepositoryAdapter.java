package greendream.cultivatech.platform.monitoring.infrastructure.persistence.jpa.adapters;

import greendream.cultivatech.platform.monitoring.domain.model.aggregates.SensorReading;
import greendream.cultivatech.platform.monitoring.domain.model.valueobjects.DeviceId;
import greendream.cultivatech.platform.monitoring.domain.repositories.SensorReadingRepository;
import greendream.cultivatech.platform.monitoring.infrastructure.persistence.jpa.entities.SensorReadingPersistenceEntity;
import greendream.cultivatech.platform.monitoring.infrastructure.persistence.jpa.mappers.SensorReadingMapper;
import greendream.cultivatech.platform.monitoring.infrastructure.persistence.jpa.repositories.SensorReadingJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * JPA adapter implementing the SensorReadingRepository domain port.
 */
@Repository
public class SensorReadingRepositoryAdapter implements SensorReadingRepository {

    private final SensorReadingJpaRepository sensorReadingJpaRepository;
    private final SensorReadingMapper mapper;

    public SensorReadingRepositoryAdapter(SensorReadingJpaRepository sensorReadingJpaRepository,
                                          SensorReadingMapper mapper) {
        this.sensorReadingJpaRepository = sensorReadingJpaRepository;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public SensorReading save(SensorReading reading) {
        SensorReadingPersistenceEntity entity = Optional.ofNullable(reading.getId())
                .map(this::findEntity)
                .orElseGet(() -> mapper.toNewEntity(reading));

        if (reading.getId() != null) {
            mapper.updateEntity(entity, reading);
        }

        return mapper.toDomain(sensorReadingJpaRepository.save(entity));
    }

    @Override
    @Transactional
    public SensorReading update(SensorReading reading) {
        if (reading.getId() == null) {
            throw new IllegalArgumentException("error.sensor.id.not-assigned");
        }
        return save(reading);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SensorReading> findAll() {
        return sensorReadingJpaRepository.findAll().stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SensorReading> findById(Long id) {
        return sensorReadingJpaRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        sensorReadingJpaRepository.delete(findEntity(id));
    }

    @Override
    @Transactional
    public void deleteAll() {
        sensorReadingJpaRepository.deleteAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SensorReading> findAllByDeviceId(DeviceId deviceId) {
        return sensorReadingJpaRepository.findAllByDeviceIdOrderByRecordedAtDesc(deviceId.value()).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SensorReading> findAllByOrderByRecordedAtDesc() {
        return sensorReadingJpaRepository.findAllByOrderByRecordedAtDesc().stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByDeviceId(DeviceId deviceId) {
        return sensorReadingJpaRepository.existsByDeviceId(deviceId.value());
    }

    private SensorReadingPersistenceEntity findEntity(Long id) {
        return sensorReadingJpaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("error.sensor.not-found"));
    }
}
