package greendream.cultivatech.platform.monitoring.application.internal.commandservices;

import greendream.cultivatech.platform.monitoring.application.commands.DeleteSensorReadingCommand;
import greendream.cultivatech.platform.monitoring.application.commands.RegisterSensorReadingCommand;
import greendream.cultivatech.platform.monitoring.application.commandservices.SensorReadingCommandService;
import greendream.cultivatech.platform.monitoring.application.results.SensorReadingError;
import greendream.cultivatech.platform.monitoring.application.results.SensorReadingResult;
import greendream.cultivatech.platform.monitoring.domain.model.aggregates.SensorReading;
import greendream.cultivatech.platform.monitoring.domain.model.valueobjects.DeviceId;
import greendream.cultivatech.platform.monitoring.domain.repositories.DeviceRepository;
import greendream.cultivatech.platform.monitoring.domain.repositories.SensorReadingRepository;
import greendream.cultivatech.platform.shared.application.result.Result;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of SensorReadingCommandService
 */
@Service
public class SensorReadingCommandServiceImpl implements SensorReadingCommandService {

    private final SensorReadingRepository sensorReadingRepository;
    private final DeviceRepository deviceRepository;

    public SensorReadingCommandServiceImpl(SensorReadingRepository sensorReadingRepository,
                                           DeviceRepository deviceRepository) {
        this.sensorReadingRepository = sensorReadingRepository;
        this.deviceRepository = deviceRepository;
    }

    @Override
    @Transactional
    public Result<SensorReadingResult, SensorReadingError> register(RegisterSensorReadingCommand command) {
        if (command == null) {
            return Result.failure(new SensorReadingError("error.command.required"));
        }

        DeviceId deviceId;
        try {
            deviceId = new DeviceId(command.deviceId());
        } catch (IllegalArgumentException exception) {
            return Result.failure(new SensorReadingError(exception.getMessage()));
        }

        if (deviceRepository.findById(deviceId).isEmpty()) {
            return Result.failure(new SensorReadingError("error.device.not-found"));
        }

        try {
            SensorReading reading = new SensorReading(
                    deviceId,
                    command.parameter(),
                    command.value(),
                    command.unit(),
                    command.recordedAt()
            );

            return Result.success(toResult(sensorReadingRepository.save(reading)));
        } catch (IllegalArgumentException exception) {
            return Result.failure(new SensorReadingError(exception.getMessage()));
        }
    }

    @Override
    @Transactional
    public Result<Void, SensorReadingError> delete(DeleteSensorReadingCommand command) {
        if (command == null) {
            return Result.failure(new SensorReadingError("error.command.required"));
        }

        Long sensorReadingId = command.sensorReadingId();
        if (sensorReadingId == null || sensorReadingId <= 0) {
            return Result.failure(new SensorReadingError("error.sensor.id.invalid"));
        }

        if (sensorReadingRepository.findById(sensorReadingId).isEmpty()) {
            return Result.failure(new SensorReadingError("error.sensor.not-found"));
        }

        sensorReadingRepository.deleteById(sensorReadingId);
        return Result.success(null);
    }

    private SensorReadingResult toResult(SensorReading reading) {
        Long id = reading.getId();
        if (id == null) {
            throw new IllegalArgumentException("error.sensor.id.not-assigned");
        }

        return new SensorReadingResult(
                id,
                reading.getDeviceId().value(),
                reading.getParameter(),
                reading.getValue(),
                reading.getUnit(),
                reading.getRecordedAt()
        );
    }
}
