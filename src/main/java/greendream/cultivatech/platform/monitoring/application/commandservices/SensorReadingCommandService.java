package greendream.cultivatech.platform.monitoring.application.commandservices;

import greendream.cultivatech.platform.monitoring.application.commands.DeleteSensorReadingCommand;
import greendream.cultivatech.platform.monitoring.application.commands.RegisterSensorReadingCommand;
import greendream.cultivatech.platform.monitoring.application.results.SensorReadingError;
import greendream.cultivatech.platform.monitoring.application.results.SensorReadingResult;
import greendream.cultivatech.platform.shared.application.result.Result;

/**
 * Application service contract for sensor reading write use cases.
 */
public interface SensorReadingCommandService {

    /**
     * Registers a reading produced by an existing device.
     *
     * @param command registration command
     * @return the persisted reading or an application error
     */
    Result<SensorReadingResult, SensorReadingError> register(RegisterSensorReadingCommand command);

    /**
     * Deletes an existing reading.
     *
     * @param command delete command
     * @return an empty success or an application error
     */
    Result<Void, SensorReadingError> delete(DeleteSensorReadingCommand command);
}
