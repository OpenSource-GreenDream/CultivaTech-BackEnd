package greendream.cultivatech.platform.monitoring.application.internal.commandservices;

import greendream.cultivatech.platform.monitoring.application.commands.DeleteDeviceCommand;
import greendream.cultivatech.platform.monitoring.application.commands.RegisterDeviceCommand;
import greendream.cultivatech.platform.monitoring.application.commands.UpdateDeviceCommand;
import greendream.cultivatech.platform.monitoring.application.commandservices.DeviceCommandService;
import greendream.cultivatech.platform.monitoring.application.results.DeviceError;
import greendream.cultivatech.platform.monitoring.application.results.DeviceResult;
import greendream.cultivatech.platform.monitoring.domain.model.aggregates.Device;
import greendream.cultivatech.platform.monitoring.domain.model.valueobjects.DeviceId;
import greendream.cultivatech.platform.monitoring.domain.model.valueobjects.DeviceStatus;
import greendream.cultivatech.platform.monitoring.domain.model.valueobjects.FieldId;
import greendream.cultivatech.platform.monitoring.domain.model.valueobjects.MacAddress;
import greendream.cultivatech.platform.monitoring.domain.repositories.DeviceRepository;
import greendream.cultivatech.platform.monitoring.domain.repositories.SensorReadingRepository;
import greendream.cultivatech.platform.shared.application.result.Result;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of DeviceCommandService
 */
@Service
public class DeviceCommandServiceImpl implements DeviceCommandService {

    private final DeviceRepository deviceRepository;
    private final SensorReadingRepository sensorReadingRepository;

    public DeviceCommandServiceImpl(DeviceRepository deviceRepository,
                                    SensorReadingRepository sensorReadingRepository) {
        this.deviceRepository = deviceRepository;
        this.sensorReadingRepository = sensorReadingRepository;
    }

    @Override
    @Transactional
    public Result<DeviceResult, DeviceError> register(RegisterDeviceCommand command) {
        if (command == null) {
            return Result.failure(new DeviceError("error.command.required"));
        }

        FieldId fieldId;
        MacAddress macAddress;
        DeviceStatus status;
        try {
            fieldId = new FieldId(command.fieldId());
            macAddress = new MacAddress(command.macAddress());
            status = new DeviceStatus(command.status());
        } catch (IllegalArgumentException exception) {
            return Result.failure(new DeviceError(exception.getMessage()));
        }

        if (deviceRepository.existsByMacAddress(macAddress)) {
            return Result.failure(new DeviceError("error.device.mac-address.already-exists"));
        }

        try {
            Device device = Device.register(fieldId, macAddress, status);
            return Result.success(toResult(deviceRepository.save(device)));
        } catch (IllegalArgumentException exception) {
            return Result.failure(new DeviceError(exception.getMessage()));
        }
    }

    @Override
    @Transactional
    public Result<DeviceResult, DeviceError> update(UpdateDeviceCommand command) {
        if (command == null) {
            return Result.failure(new DeviceError("error.command.required"));
        }

        DeviceId deviceId;
        try {
            deviceId = new DeviceId(command.deviceId());
        } catch (IllegalArgumentException exception) {
            return Result.failure(new DeviceError(exception.getMessage()));
        }

        Device device = deviceRepository.findById(deviceId).orElse(null);
        if (device == null) {
            return Result.failure(new DeviceError("error.device.not-found"));
        }

        try {
            device.changeStatus(new DeviceStatus(command.status()));
            return Result.success(toResult(deviceRepository.update(device)));
        } catch (IllegalArgumentException exception) {
            return Result.failure(new DeviceError(exception.getMessage()));
        }
    }

    @Override
    @Transactional
    public Result<Void, DeviceError> delete(DeleteDeviceCommand command) {
        if (command == null) {
            return Result.failure(new DeviceError("error.command.required"));
        }

        DeviceId deviceId;
        try {
            deviceId = new DeviceId(command.deviceId());
        } catch (IllegalArgumentException exception) {
            return Result.failure(new DeviceError(exception.getMessage()));
        }

        if (deviceRepository.findById(deviceId).isEmpty()) {
            return Result.failure(new DeviceError("error.device.not-found"));
        }

        if (sensorReadingRepository.existsByDeviceId(deviceId)) {
            return Result.failure(new DeviceError("error.device.in-use"));
        }

        deviceRepository.deleteById(deviceId);
        return Result.success(null);
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
