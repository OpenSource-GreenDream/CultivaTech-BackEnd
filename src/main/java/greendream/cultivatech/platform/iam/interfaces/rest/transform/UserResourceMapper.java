package greendream.cultivatech.platform.iam.interfaces.rest.transform;

import greendream.cultivatech.platform.iam.application.results.SignUpResult;
import greendream.cultivatech.platform.iam.interfaces.rest.resources.TemporaryProfileResource;
import greendream.cultivatech.platform.iam.interfaces.rest.resources.UserResource;
import org.springframework.stereotype.Component;

/**
 * Maps application results to REST resources.
 */
@Component
public class UserResourceMapper {

    public UserResource toResource(SignUpResult result) {
        return new UserResource(
                result.userId(),
                result.email(),
                result.roles(),
                new TemporaryProfileResource(
                        result.profile().fundoName(),
                        result.profile().contactPhone(),
                        result.profile().moistureThreshold(),
                        result.profile().tempThreshold()
                )
        );
    }
}
