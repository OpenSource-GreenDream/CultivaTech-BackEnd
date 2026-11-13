package greendream.cultivatech.platform.iam.interfaces.rest.transform;

import greendream.cultivatech.platform.iam.application.results.UserResult;
import greendream.cultivatech.platform.iam.interfaces.rest.resources.UserSummaryResource;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Maps user application results to REST resources.
 */
@Component
public class UserSummaryResourceMapper {

    public UserSummaryResource toResource(UserResult result) {
        return new UserSummaryResource(
                result.id(),
                result.email(),
                result.roles(),
                result.active()
        );
    }

    public List<UserSummaryResource> toResources(List<UserResult> results) {
        return results.stream()
                .map(this::toResource)
                .toList();
    }
}
