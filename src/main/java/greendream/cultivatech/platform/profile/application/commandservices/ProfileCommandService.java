package greendream.cultivatech.platform.profile.application.commandservices;

import greendream.cultivatech.platform.profile.application.commands.RegisterProfileCommand;
import greendream.cultivatech.platform.profile.application.commands.UpdateProfileCommand;
import greendream.cultivatech.platform.profile.application.results.ProfileError;
import greendream.cultivatech.platform.profile.application.results.ProfileResult;
import greendream.cultivatech.platform.shared.application.result.Result;

/**
 * Application service contract for profile write use cases.
 */
public interface ProfileCommandService {

    /**
     * Registers the profile of a user.
     *
     * @param command registration command
     * @return the persisted profile or an application error
     */
    Result<ProfileResult, ProfileError> register(RegisterProfileCommand command);

    /**
     * Replaces the editable data of an existing profile.
     *
     * @param command update command
     * @return the updated profile or an application error
     */
    Result<ProfileResult, ProfileError> update(UpdateProfileCommand command);
}
