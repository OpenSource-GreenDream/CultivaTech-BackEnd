package greendream.cultivatech.platform.analytics.application.ports;
import greendream.cultivatech.platform.monitoring.domain.model.aggregates.SensorReading;
import java.util.List;
public interface DeviceTelemetry { boolean deviceExists(Long deviceId); List<SensorReading> readings(Long deviceId); }
