package greendream.cultivatech.platform.monitoring.application.internal.queryservices;

import greendream.cultivatech.platform.monitoring.application.queries.GetAllSensorReadingsQuery;
import greendream.cultivatech.platform.monitoring.application.queries.GetSensorReadingsByDeviceIdQuery;
import greendream.cultivatech.platform.monitoring.application.querieservices.SensorReadingQueryService;
import greendream.cultivatech.platform.monitoring.application.results.SensorReadingError;
import greendream.cultivatech.platform.monitoring.application.results.SensorReadingResult;
import greendream.cultivatech.platform.monitoring.domain.model.aggregates.SensorReading;
import greendream.cultivatech.platform.monitoring.domain.model.valueobjects.DeviceId;
import greendream.cultivatech.platform.monitoring.domain.repositories.SensorReadingRepository;
import greendream.cultivatech.platform.shared.application.result.Result;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementation of SensorReadingQueryService
 */
@Service
public class SensorReadingQueryServiceImpl implements SensorReadingQueryService {

    private final SensorReadingRepository sensorReadingRepository;

    public SensorReadingQueryServiceImpl(SensorReadingRepository sensorReadingRepository) {
        this.sensorReadingRepository = sensorReadingRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Result<List<SensorReadingResult>, SensorReadingError> getAllSensorReadings(
            GetAllSensorReadingsQuery query) {

        if (query == null) {
            return Result.failure(new SensorReadingError("error.command.required"));
        }

        List<SensorReadingResult> readings = sensorReadingRepository.findAllByOrderByRecordedAtDesc().stream()
                .map(this::toResult)
                .toList();

        return Result.success(readings);
    }

    @Override
    @Transactional(readOnly = true)
    public Result<List<SensorReadingResult>, SensorReadingError> getByDeviceId(
            GetSensorReadingsByDeviceIdQuery query) {

        if (query == null) {
            return Result.failure(new SensorReadingError("error.command.required"));
        }

        DeviceId deviceId;
        try {
            deviceId = new DeviceId(query.deviceId());
        } catch (IllegalArgumentException exception) {
            return Result.failure(new SensorReadingError(exception.getMessage()));
        }

        List<SensorReadingResult> readings = sensorReadingRepository.findAllByDeviceId(deviceId).stream()
                .map(this::toResult)
                .toList();

        return Result.success(readings);
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
