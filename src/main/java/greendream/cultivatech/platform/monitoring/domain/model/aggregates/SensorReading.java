package greendream.cultivatech.platform.monitoring.domain.model.aggregates;

import greendream.cultivatech.platform.monitoring.domain.model.valueobjects.DeviceId;
import greendream.cultivatech.platform.shared.domain.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter;

import java.util.Date;

/**
 * Sensor reading aggregate root.
 * Represents a measurement taken by an IoT device installed on a field.
 */
public class SensorReading extends AbstractDomainAggregateRoot<SensorReading> {

    @Getter
    private Long id;
    @Getter
    private final DeviceId deviceId;
    @Getter
    private final String parameter;
    @Getter
    private final Double value;
    @Getter
    private final String unit;
    private final Date recordedAt;
    private Date createdAt;

    /** Represents a sensor reading. */
    public SensorReading(DeviceId deviceId, String parameter,
                         Double value, String unit, Date recordedAt) {

        if (deviceId == null)
            throw new IllegalArgumentException("error.sensor.device-id.required");

        if (parameter == null || parameter.isBlank())
            throw new IllegalArgumentException("error.sensor.parameter.required");

        if (value == null || !Double.isFinite(value))
            throw new IllegalArgumentException("error.sensor.value.invalid");

        if (unit == null || unit.isBlank())
            throw new IllegalArgumentException("error.sensor.unit.required");

        if (recordedAt == null)
            throw new IllegalArgumentException("error.sensor.recorded-at.required");

        this.deviceId = deviceId;
        this.parameter = parameter.trim();
        this.value = value;
        this.unit = unit.trim();
        this.recordedAt = new Date(recordedAt.getTime());
    }

    public static SensorReading restore(
            Long id,
            DeviceId deviceId,
            String parameter,
            Double value,
            String unit,
            Date recordedAt,
            Date createdAt) {

        SensorReading reading = new SensorReading(
                deviceId, parameter, value, unit, recordedAt
        );

        reading.id = id;
        reading.createdAt = createdAt == null
                ? null : new Date(createdAt.getTime());

        return reading;
    }

    public Date getRecordedAt() {
        return new Date(recordedAt.getTime());
    }

    public Date getCreatedAt() {
        return createdAt == null ? null : new Date(createdAt.getTime());
    }
}
