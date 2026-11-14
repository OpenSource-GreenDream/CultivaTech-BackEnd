package greendream.cultivatech.platform.monitoring.application.commandservices;

import greendream.cultivatech.platform.monitoring.application.commands.DeleteDeviceCommand;
import greendream.cultivatech.platform.monitoring.application.commands.RegisterDeviceCommand;
import greendream.cultivatech.platform.monitoring.application.commands.UpdateDeviceCommand;
import greendream.cultivatech.platform.monitoring.application.results.DeviceError;
import greendream.cultivatech.platform.monitoring.application.results.DeviceResult;
import greendream.cultivatech.platform.shared.application.result.Result;

/**
 * Application service contract for device write use cases.
 */
public interface DeviceCommandService {

    /**
     * Registers a device on a field.
     *
     * @param command registration command
     * @return the persisted device or an application error
     */
    Result<DeviceResult, DeviceError> register(RegisterDeviceCommand command);

    /**
     * Updates the operational status of an existing device.
     *
     * @param command update command
     * @return the updated device or an application error
     */
    Result<DeviceResult, DeviceError> update(UpdateDeviceCommand command);

    /**
     * Deletes a device that has no sensor readings.
     *
     * @param command delete command
     * @return an empty success or an application error
     */
    Result<Void, DeviceError> delete(DeleteDeviceCommand command);
}
