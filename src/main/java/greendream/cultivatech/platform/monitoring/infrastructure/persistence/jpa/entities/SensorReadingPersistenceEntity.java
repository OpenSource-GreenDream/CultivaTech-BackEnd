package greendream.cultivatech.platform.monitoring.infrastructure.persistence.jpa.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Date;

/**
 * Persistence representation of the sensor_readings table.
 * The owning device is stored as a plain identifier because the Monitoring context
 * references it through a local DeviceId.
 */
@Setter
@Getter
@Entity
@Table(
        name = "sensor_readings",
        indexes = @Index(
                name = "idx_sensor_readings_device_recorded_at",
                columnList = "device_id, recorded_at"
        )
)
@EntityListeners(AuditingEntityListener.class)
public class SensorReadingPersistenceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "device_id", nullable = false)
    private Long deviceId;

    @Column(name = "parameter", nullable = false, length = 30)
    private String parameter;

    @Column(name = "value", nullable = false)
    private double value;

    @Column(name = "unit", nullable = false, length = 10)
    private String unit;

    @Column(name = "recorded_at", nullable = false)
    private Date recordedAt;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Date createdAt;

    protected SensorReadingPersistenceEntity() {
    }

    public SensorReadingPersistenceEntity(Long deviceId,
                                          String parameter,
                                          double value,
                                          String unit,
                                          Date recordedAt) {
        this.deviceId = deviceId;
        this.parameter = parameter;
        this.value = value;
        this.unit = unit;
        this.recordedAt = recordedAt;
    }
}
