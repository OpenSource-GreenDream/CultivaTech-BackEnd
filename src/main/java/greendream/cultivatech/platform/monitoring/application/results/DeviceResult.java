package greendream.cultivatech.platform.monitoring.application.results;

import java.util.Date;

/**
 * Application-level representation of a device.
 *
 * @param id         persisted device identity
 * @param fieldId    field where the device is installed
 * @param macAddress normalized MAC address
 * @param status     operational status
 * @param lastSync   last synchronization timestamp, or {@code null}
 */
public record DeviceResult(
        Long id,
        Long fieldId,
        String macAddress,
        String status,
        Date lastSync
) {
}
