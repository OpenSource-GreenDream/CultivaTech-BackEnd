package greendream.cultivatech.platform.monitoring.interfaces.rest.resources;

import java.util.Date;

/**
 * HTTP response representing a device.
 */
public record DeviceResource(
        Long id,
        Long fieldId,
        String macAddress,
        String status,
        Date lastSync
) {
}
