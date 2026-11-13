package greendream.cultivatech.platform.iam.interfaces.rest.transform;

import greendream.cultivatech.platform.iam.application.results.RoleResult;
import greendream.cultivatech.platform.iam.interfaces.rest.resources.RoleResource;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Maps role application results to REST resources.
 */
@Component
public class RoleResourceMapper {

    public RoleResource toResource(RoleResult result) {
        return new RoleResource(
                result.id(),
                result.name(),
                result.description()
        );
    }

    public List<RoleResource> toResources(List<RoleResult> results) {
        return results.stream()
                .map(this::toResource)
                .toList();
    }
}
