package greendream.cultivatech.platform.iam.infrastructure.integration.profile;

import greendream.cultivatech.platform.iam.application.commands.ProfileRegistrationCommand;
import greendream.cultivatech.platform.iam.application.ports.ProfileRegistration;
import greendream.cultivatech.platform.iam.application.results.ProfileRegistrationError;
import greendream.cultivatech.platform.iam.application.results.ProfileRegistrationResult;
import greendream.cultivatech.platform.profile.application.commands.RegisterProfileCommand;
import greendream.cultivatech.platform.profile.application.commandservices.ProfileCommandService;
import greendream.cultivatech.platform.profile.application.results.ProfileError;
import greendream.cultivatech.platform.profile.application.results.ProfileResult;
import greendream.cultivatech.platform.shared.application.result.Result;
import org.springframework.stereotype.Component;

/**
 * Anti-corruption adapter that connects the IAM profile registration port with the
 * Profile bounded context.
 */
@Component
public class ProfileRegistrationAdapter implements ProfileRegistration {

    private final ProfileCommandService profileCommandService;

    public ProfileRegistrationAdapter(ProfileCommandService profileCommandService) {
        this.profileCommandService = profileCommandService;
    }

    @Override
    public Result<ProfileRegistrationResult, ProfileRegistrationError> register(ProfileRegistrationCommand command) {
        Result<ProfileResult, ProfileError> result = profileCommandService.register(
                new RegisterProfileCommand(
                        command.userId(),
                        command.fundoName(),
                        command.contactPhone(),
                        command.moistureThreshold(),
                        command.temperatureThreshold()
                )
        );

        return result.fold(
                profile -> Result.success(new ProfileRegistrationResult(
                        profile.fundoName(),
                        profile.contactPhone(),
                        profile.moistureThreshold(),
                        profile.temperatureThreshold()
                )),
                error -> Result.failure(new ProfileRegistrationError(error.messageKey()))
        );
    }
}
