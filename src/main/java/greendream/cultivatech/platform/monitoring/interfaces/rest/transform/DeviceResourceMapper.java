package greendream.cultivatech.platform.monitoring.interfaces.rest.transform;

import greendream.cultivatech.platform.monitoring.application.results.DeviceResult;
import greendream.cultivatech.platform.monitoring.interfaces.rest.resources.DeviceResource;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Maps device application results to REST resources.
 */
@Component
public class DeviceResourceMapper {

    public DeviceResource toResource(DeviceResult result) {
        return new DeviceResource(
                result.id(),
                result.fieldId(),
                result.macAddress(),
                result.status(),
                result.lastSync()
        );
    }

    public List<DeviceResource> toResources(List<DeviceResult> results) {
        return results.stream()
                .map(this::toResource)
                .toList();
    }
}
