package greendream.cultivatech.platform.monitoring.application.internal.queryservices;

import greendream.cultivatech.platform.monitoring.application.queries.GetAllDevicesQuery;
import greendream.cultivatech.platform.monitoring.application.queries.GetDeviceByIdQuery;
import greendream.cultivatech.platform.monitoring.application.queries.GetDevicesByFieldIdQuery;
import greendream.cultivatech.platform.monitoring.application.querieservices.DeviceQueryService;
import greendream.cultivatech.platform.monitoring.application.results.DeviceError;
import greendream.cultivatech.platform.monitoring.application.results.DeviceResult;
import greendream.cultivatech.platform.monitoring.domain.model.aggregates.Device;
import greendream.cultivatech.platform.monitoring.domain.model.valueobjects.DeviceId;
import greendream.cultivatech.platform.monitoring.domain.model.valueobjects.FieldId;
import greendream.cultivatech.platform.monitoring.domain.repositories.DeviceRepository;
import greendream.cultivatech.platform.shared.application.result.Result;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementation of DeviceQueryService
 */
@Service
public class DeviceQueryServiceImpl implements DeviceQueryService {

    private final DeviceRepository deviceRepository;

    public DeviceQueryServiceImpl(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Result<List<DeviceResult>, DeviceError> getAllDevices(GetAllDevicesQuery query) {
        if (query == null) {
            return Result.failure(new DeviceError("error.command.required"));
        }

        List<DeviceResult> devices = deviceRepository.findAll().stream()
                .map(this::toResult)
                .toList();
        return Result.success(devices);
    }

    @Override
    @Transactional(readOnly = true)
    public Result<DeviceResult, DeviceError> getById(GetDeviceByIdQuery query) {
        if (query == null) {
            return Result.failure(new DeviceError("error.command.required"));
        }

        DeviceId deviceId;
        try {
            deviceId = new DeviceId(query.deviceId());
        } catch (IllegalArgumentException exception) {
            return Result.failure(new DeviceError(exception.getMessage()));
        }

        Device device = deviceRepository.findById(deviceId).orElse(null);
        if (device == null) {
            return Result.failure(new DeviceError("error.device.not-found"));
        }

        return Result.success(toResult(device));
    }

    @Override
    @Transactional(readOnly = true)
    public Result<List<DeviceResult>, DeviceError> getByFieldId(GetDevicesByFieldIdQuery query) {
        if (query == null) {
            return Result.failure(new DeviceError("error.command.required"));
        }

        FieldId fieldId;
        try {
            fieldId = new FieldId(query.fieldId());
        } catch (IllegalArgumentException exception) {
            return Result.failure(new DeviceError(exception.getMessage()));
        }

        List<DeviceResult> devices = deviceRepository.findByFieldId(fieldId).stream()
                .map(this::toResult)
                .toList();
        return Result.success(devices);
    }

    private DeviceResult toResult(Device device) {
        Long id = device.id()
                .map(DeviceId::value)
                .orElseThrow(() -> new IllegalArgumentException("error.device.id.not-assigned"));

        return new DeviceResult(
                id,
                device.fieldId().value(),
                device.macAddress().value(),
                device.status().value(),
                device.lastSync()
        );
    }
}
