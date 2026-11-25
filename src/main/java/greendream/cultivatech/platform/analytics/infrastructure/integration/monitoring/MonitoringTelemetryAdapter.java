package greendream.cultivatech.platform.analytics.infrastructure.integration.monitoring;
import greendream.cultivatech.platform.analytics.application.ports.DeviceTelemetry;
import greendream.cultivatech.platform.monitoring.domain.model.valueobjects.DeviceId;
import greendream.cultivatech.platform.monitoring.domain.repositories.DeviceRepository;
import greendream.cultivatech.platform.monitoring.domain.repositories.SensorReadingRepository;
import greendream.cultivatech.platform.monitoring.domain.model.aggregates.SensorReading;
import org.springframework.stereotype.Component;
import java.util.List;
@Component public class MonitoringTelemetryAdapter implements DeviceTelemetry {
 private final DeviceRepository devices; private final SensorReadingRepository readings;
 public MonitoringTelemetryAdapter(DeviceRepository devices,SensorReadingRepository readings){this.devices=devices;this.readings=readings;}
 public boolean deviceExists(Long id){return devices.findById(new DeviceId(id)).isPresent();}
 public List<SensorReading> readings(Long id){return readings.findAllByDeviceId(new DeviceId(id));}
}
