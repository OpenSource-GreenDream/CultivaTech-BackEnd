package greendream.cultivatech.platform.profile.interfaces.rest.transform;

import greendream.cultivatech.platform.profile.application.results.ProfileResult;
import greendream.cultivatech.platform.profile.interfaces.rest.resources.ProfileResource;
import org.springframework.stereotype.Component;

/**
 * Maps application results to REST resources.
 */
@Component
public class ProfileResourceMapper {

    public ProfileResource toResource(ProfileResult result) {
        return new ProfileResource(
                result.id(),
                result.userId(),
                result.fundoName(),
                result.contactPhone(),
                result.moistureThreshold(),
                result.temperatureThreshold()
        );
    }
}
