package greendream.cultivatech.platform.monitoring.application.querieservices;

import greendream.cultivatech.platform.monitoring.application.queries.GetAllDevicesQuery;
import greendream.cultivatech.platform.monitoring.application.queries.GetDeviceByIdQuery;
import greendream.cultivatech.platform.monitoring.application.queries.GetDevicesByFieldIdQuery;
import greendream.cultivatech.platform.monitoring.application.results.DeviceError;
import greendream.cultivatech.platform.monitoring.application.results.DeviceResult;
import greendream.cultivatech.platform.shared.application.result.Result;

import java.util.List;

/**
 * Application service contract for device read use cases.
 */
public interface DeviceQueryService {

    /**
     * Lists every device.
     *
     * @param query list query
     * @return the devices or an application error
     */
    Result<List<DeviceResult>, DeviceError> getAllDevices(GetAllDevicesQuery query);

    /**
     * Retrieves one device.
     *
     * @param query lookup query
     * @return the device or an application error
     */
    Result<DeviceResult, DeviceError> getById(GetDeviceByIdQuery query);

    /**
     * Lists the devices installed on a field.
     *
     * @param query lookup query
     * @return the devices or an application error
     */
    Result<List<DeviceResult>, DeviceError> getByFieldId(GetDevicesByFieldIdQuery query);
}
