package greendream.cultivatech.platform.monitoring.application.querieservices;

import greendream.cultivatech.platform.monitoring.application.queries.GetAllSensorReadingsQuery;
import greendream.cultivatech.platform.monitoring.application.queries.GetSensorReadingsByDeviceIdQuery;
import greendream.cultivatech.platform.monitoring.application.results.SensorReadingError;
import greendream.cultivatech.platform.monitoring.application.results.SensorReadingResult;
import greendream.cultivatech.platform.shared.application.result.Result;

import java.util.List;

/**
 * Application service contract for sensor reading read use cases.
 */
public interface SensorReadingQueryService {

    /**
     * Retrieves every sensor reading.
     *
     * @param query list query
     * @return the readings or an application error
     */
    Result<List<SensorReadingResult>, SensorReadingError> getAllSensorReadings(GetAllSensorReadingsQuery query);

    /**
     * Retrieves every reading recorded by a device.
     *
     * @param query lookup query
     * @return the readings or an application error
     */
    Result<List<SensorReadingResult>, SensorReadingError> getByDeviceId(GetSensorReadingsByDeviceIdQuery query);
}
