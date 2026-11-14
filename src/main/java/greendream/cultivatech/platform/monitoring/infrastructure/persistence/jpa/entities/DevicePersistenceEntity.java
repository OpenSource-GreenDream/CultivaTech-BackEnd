package greendream.cultivatech.platform.monitoring.infrastructure.persistence.jpa.entities;

import greendream.cultivatech.platform.shared.infrastructure.persistence.jpa.AuditableAbstractPersistenceEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * Persistence representation of the devices table.
 * The owning field is stored as a plain identifier because the Monitoring context
 * references it through a local FieldId; JPA associations between contexts
 * would leak the persistence graph into the domain. The MAC address is unique.
 */
@Setter
@Getter
@Entity
@Table(
        name = "devices",
        uniqueConstraints = @UniqueConstraint(name = "uk_devices_mac_address", columnNames = "mac_address"),
        indexes = @Index(name = "idx_devices_field_id", columnList = "field_id")
)
public class DevicePersistenceEntity extends AuditableAbstractPersistenceEntity {

    @Column(name = "field_id", nullable = false)
    private Long fieldId;

    @Column(name = "mac_address", nullable = false, length = 17)
    private String macAddress;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "last_sync")
    private Date lastSync;

    protected DevicePersistenceEntity() {
    }

    public DevicePersistenceEntity(Long fieldId,
                                   String macAddress,
                                   String status,
                                   Date lastSync) {
        this.fieldId = fieldId;
        this.macAddress = macAddress;
        this.status = status;
        this.lastSync = lastSync;
    }
}
