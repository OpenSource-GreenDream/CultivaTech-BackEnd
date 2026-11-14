package greendream.cultivatech.platform.monitoring.interfaces.rest.transform;

import greendream.cultivatech.platform.monitoring.application.results.SensorReadingResult;
import greendream.cultivatech.platform.monitoring.interfaces.rest.resources.SensorReadingResource;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Maps sensor reading application results to REST resources.
 */
@Component
public class SensorReadingResourceMapper {

    public SensorReadingResource toResource(SensorReadingResult result) {
        return new SensorReadingResource(
                result.id(),
                result.deviceId(),
                result.parameter(),
                result.value(),
                result.unit(),
                result.recordedAt()
        );
    }

    public List<SensorReadingResource> toResources(List<SensorReadingResult> results) {
        return results.stream()
                .map(this::toResource)
                .toList();
    }
}
