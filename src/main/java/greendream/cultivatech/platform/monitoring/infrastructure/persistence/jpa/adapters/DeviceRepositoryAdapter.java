package greendream.cultivatech.platform.monitoring.infrastructure.persistence.jpa.adapters;

import greendream.cultivatech.platform.monitoring.domain.model.aggregates.Device;
import greendream.cultivatech.platform.monitoring.domain.model.valueobjects.DeviceId;
import greendream.cultivatech.platform.monitoring.domain.model.valueobjects.FieldId;
import greendream.cultivatech.platform.monitoring.domain.model.valueobjects.MacAddress;
import greendream.cultivatech.platform.monitoring.domain.repositories.DeviceRepository;
import greendream.cultivatech.platform.monitoring.infrastructure.persistence.jpa.entities.DevicePersistenceEntity;
import greendream.cultivatech.platform.monitoring.infrastructure.persistence.jpa.mappers.DeviceMapper;
import greendream.cultivatech.platform.monitoring.infrastructure.persistence.jpa.repositories.DeviceJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * JPA adapter implementing the DeviceRepository domain port.
 */
@Repository
public class DeviceRepositoryAdapter implements DeviceRepository {

    private final DeviceJpaRepository deviceJpaRepository;
    private final DeviceMapper mapper;

    public DeviceRepositoryAdapter(DeviceJpaRepository deviceJpaRepository, DeviceMapper mapper) {
        this.deviceJpaRepository = deviceJpaRepository;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public Device save(Device device) {
        DevicePersistenceEntity entity = device.id()
                .map(this::findEntity)
                .orElseGet(() -> mapper.toNewEntity(device));

        if (device.id().isPresent()) {
            mapper.updateEntity(entity, device);
        }

        return mapper.toDomain(deviceJpaRepository.save(entity));
    }

    @Override
    @Transactional
    public Device update(Device device) {
        if (device.id().isEmpty()) {
            throw new IllegalArgumentException("error.device.id.not-assigned");
        }
        return save(device);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Device> findAll() {
        return deviceJpaRepository.findAll().stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Device> findById(DeviceId id) {
        return deviceJpaRepository.findById(id.value())
                .map(mapper::toDomain);
    }

    @Override
    @Transactional
    public void deleteById(DeviceId id) {
        deviceJpaRepository.delete(findEntity(id));
    }

    @Override
    @Transactional
    public void deleteAll() {
        deviceJpaRepository.deleteAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Device> findByMacAddress(MacAddress macAddress) {
        return deviceJpaRepository.findByMacAddress(macAddress.value())
                .map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByMacAddress(MacAddress macAddress) {
        return deviceJpaRepository.existsByMacAddress(macAddress.value());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Device> findByFieldId(FieldId fieldId) {
        return deviceJpaRepository.findByFieldId(fieldId.value()).stream()
                .map(mapper::toDomain)
                .toList();
    }

    private DevicePersistenceEntity findEntity(DeviceId id) {
        return deviceJpaRepository.findById(id.value())
                .orElseThrow(() -> new IllegalArgumentException("error.device.not-found"));
    }
}
