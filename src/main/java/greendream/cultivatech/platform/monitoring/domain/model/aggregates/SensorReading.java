package greendream.cultivatech.platform.monitoring.domain.model.aggregates;

import greendream.cultivatech.platform.monitoring.domain.model.enums.SensorParameter;
import greendream.cultivatech.platform.shared.domain.exceptions.InvalidSensorUnitException;
import greendream.cultivatech.platform.shared.domain.exceptions.InvalidSensorTimestampException;
import greendream.cultivatech.platform.shared.domain.exceptions.InvalidSensorValueException;
import greendream.cultivatech.platform.shared.domain.exceptions.RequiredSensorPropertyException;
import greendream.cultivatech.platform.monitoring.domain.model.valueobjects.DeviceId;
import greendream.cultivatech.platform.shared.domain.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter;

import java.util.Date;

public class SensorReading extends AbstractDomainAggregateRoot<SensorReading> {

    @Getter
    private Long id;
    @Getter
    private final DeviceId deviceId;
    @Getter
    private final SensorParameter parameter;
    @Getter
    private final Double value;
    @Getter
    private final String unit;
    private final Date recordedAt;
    private Date createdAt;

    public SensorReading(DeviceId deviceId, SensorParameter parameter,
                         Double value, String unit, Date recordedAt) {

        if (deviceId == null)
            throw new IllegalArgumentException("error.sensor.device-id.required");

        if (parameter == null)
            throw new RequiredSensorPropertyException("REQUIRED_SENSOR_PARAMETER", "parameter");

        if (value == null)
            throw new RequiredSensorPropertyException("REQUIRED_SENSOR_VALUE", "value");

        if (!Double.isFinite(value))
            throw new InvalidSensorValueException(parameter.name(), value, null, null);

        if (unit == null || unit.isBlank())
            throw new RequiredSensorPropertyException("REQUIRED_SENSOR_UNIT", "unit");

        if (!parameter.isCompatibleUnit(unit.trim()))
            throw new InvalidSensorUnitException(parameter.name(), unit.trim(), parameter.getCanonicalUnit());

        if (!parameter.isValidValue(value)) {
            Double min = 0.0;
            Double max = null;
            if (parameter == SensorParameter.MOISTURE) {
                max = 100.0;
            } else if (parameter == SensorParameter.PH) {
                max = 14.0;
            }
            throw new InvalidSensorValueException(parameter.name(), value, min, max);
        }

        if (recordedAt == null)
            throw new InvalidSensorTimestampException();

        this.deviceId = deviceId;
        this.parameter = parameter;
        this.value = value;
        this.unit = unit.trim();
        this.recordedAt = new Date(recordedAt.getTime());
    }

    public static SensorReading restore(
            Long id,
            DeviceId deviceId,
            SensorParameter parameter,
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
