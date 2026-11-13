package greendream.cultivatech.platform.iam.interfaces.rest.transform;

import greendream.cultivatech.platform.iam.application.results.SignInResult;
import greendream.cultivatech.platform.iam.interfaces.rest.resources.SignInResource;
import org.springframework.stereotype.Component;

/**
 * Maps sign-in application results to REST resources.
 */
@Component
public class SignInResourceMapper {

    public SignInResource toResource(SignInResult result) {
        return new SignInResource(
                result.accessToken(),
                result.tokenType(),
                result.expiresIn(),
                result.userId(),
                result.email(),
                result.roles()
        );
    }
}
