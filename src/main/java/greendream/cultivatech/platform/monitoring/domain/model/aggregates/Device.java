package greendream.cultivatech.platform.monitoring.domain.model.aggregates;

import greendream.cultivatech.platform.monitoring.domain.model.valueobjects.DeviceId;
import greendream.cultivatech.platform.monitoring.domain.model.valueobjects.DeviceStatus;
import greendream.cultivatech.platform.monitoring.domain.model.valueobjects.FieldId;
import greendream.cultivatech.platform.monitoring.domain.model.valueobjects.MacAddress;
import greendream.cultivatech.platform.shared.domain.aggregates.AbstractDomainAggregateRoot;

import java.util.Date;
import java.util.Objects;
import java.util.Optional;

/**
 * Device aggregate root.
 * Represents an IoT device installed on a field. Devices are the source of sensor
 * readings, so a reading is always associated with a device identity.
 */
public final class Device extends AbstractDomainAggregateRoot<Device> {

    private DeviceId id;
    private final FieldId fieldId;
    private final MacAddress macAddress;
    private DeviceStatus status;
    private Date lastSync;

    private Device(DeviceId id,
                   FieldId fieldId,
                   MacAddress macAddress,
                   DeviceStatus status,
                   Date lastSync) {
        this.id = id;
        this.fieldId = Objects.requireNonNull(fieldId, "fieldId must not be null");
        this.macAddress = Objects.requireNonNull(macAddress, "macAddress must not be null");
        this.status = Objects.requireNonNull(status, "status must not be null");
        this.lastSync = copy(lastSync);
    }

    /**
     * Registers a new device that has not been persisted yet.
     * A new device has never synchronized, so its last synchronization is empty.
     */
    public static Device register(FieldId fieldId,
                                  MacAddress macAddress,
                                  DeviceStatus status) {
        return new Device(null, fieldId, macAddress, status, null);
    }

    /**
     * Rebuilds a persisted device.
     */
    public static Device reconstitute(DeviceId id,
                                      FieldId fieldId,
                                      MacAddress macAddress,
                                      DeviceStatus status,
                                      Date lastSync) {
        return new Device(
                Objects.requireNonNull(id, "id must not be null"),
                fieldId,
                macAddress,
                status,
                lastSync
        );
    }

    public Optional<DeviceId> id() {
        return Optional.ofNullable(id);
    }

    public FieldId fieldId() {
        return fieldId;
    }

    public MacAddress macAddress() {
        return macAddress;
    }

    public DeviceStatus status() {
        return status;
    }

    /**
     * @return the last synchronization timestamp, or {@code null} when the device has
     * never synchronized
     */
    public Date lastSync() {
        return copy(lastSync);
    }

    /**
     * @return true when this device is installed on the given field
     */
    public boolean belongsTo(FieldId fieldId) {
        return this.fieldId.equals(Objects.requireNonNull(fieldId, "fieldId must not be null"));
    }

    public void changeStatus(DeviceStatus newStatus) {
        this.status = Objects.requireNonNull(newStatus, "status must not be null");
    }

    /**
     * Records that the device synchronized at the given instant.
     */
    public void recordSync(Date syncAt) {
        if (syncAt == null) {
            throw new IllegalArgumentException("error.device.last-sync.required");
        }
        this.lastSync = copy(syncAt);
    }

    private static Date copy(Date date) {
        return date == null ? null : new Date(date.getTime());
    }
}
