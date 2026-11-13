package greendream.cultivatech.platform.profile.interfaces.rest.transform;

import greendream.cultivatech.platform.profile.application.results.FieldResult;
import greendream.cultivatech.platform.profile.interfaces.rest.resources.FieldResource;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Maps field application results to REST resources.
 */
@Component
public class FieldResourceMapper {

    public FieldResource toResource(FieldResult result) {
        return new FieldResource(
                result.id(),
                result.profileId(),
                result.name(),
                result.sizeM2(),
                result.soilType(),
                result.latitude(),
                result.longitude()
        );
    }

    public List<FieldResource> toResources(List<FieldResult> results) {
        return results.stream()
                .map(this::toResource)
                .toList();
    }
}
