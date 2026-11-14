
package greendream.cultivatech.platform.monitoring.domain.model.aggregates;

import greendream.cultivatech.platform.shared.domain.aggregates.AbstractDomainAggregateRoot;

import java.util.Date;

public class SensorReading extends AbstractDomainAggregateRoot<SensorReading> {

    private Long id;
    private final Long deviceId;
    private final String parameter;
    private final Double value;
    private final String unit;
    private final Date recordedAt;
    private Date createdAt;

    /** Represents a sensor reading. */
    public SensorReading(Long deviceId, String parameter,
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
            Long deviceId,
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

    public Long getId() { return id; }
    public Long getDeviceId() { return deviceId; }
    public String getParameter() { return parameter; }
    public Double getValue() { return value; }
    public String getUnit() { return unit; }

    public Date getRecordedAt() {
        return new Date(recordedAt.getTime());
    }

    public Date getCreatedAt() {
        return createdAt == null ? null : new Date(createdAt.getTime());
    }
}
