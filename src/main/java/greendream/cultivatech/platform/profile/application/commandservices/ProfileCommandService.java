package greendream.cultivatech.platform.profile.application.commandservices;

import greendream.cultivatech.platform.profile.application.commands.RegisterProfileCommand;
import greendream.cultivatech.platform.profile.application.commands.UpdateProfileCommand;
import greendream.cultivatech.platform.profile.application.results.ProfileError;
import greendream.cultivatech.platform.profile.application.results.ProfileResult;
import greendream.cultivatech.platform.profile.domain.model.aggregates.Profile;
import greendream.cultivatech.platform.profile.domain.model.valueobjects.ContactPhone;
import greendream.cultivatech.platform.profile.domain.model.valueobjects.FundoName;
import greendream.cultivatech.platform.profile.domain.model.valueobjects.MoistureThreshold;
import greendream.cultivatech.platform.profile.domain.model.valueobjects.ProfileId;
import greendream.cultivatech.platform.profile.domain.model.valueobjects.TemperatureThreshold;
import greendream.cultivatech.platform.profile.domain.model.valueobjects.UserId;
import greendream.cultivatech.platform.profile.domain.repositories.ProfileRepository;
import greendream.cultivatech.platform.shared.application.result.Result;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Application service for profile write use cases.
 */
@Service
public class ProfileCommandService {

    private final ProfileRepository profileRepository;

    public ProfileCommandService(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    /**
     * Registers the profile of a user.
     *
     * @param command registration command
     * @return the persisted profile or an application error
     */
    @Transactional
    public Result<ProfileResult, ProfileError> register(RegisterProfileCommand command) {
        if (command == null) {
            return Result.failure(new ProfileError("error.command.required"));
        }

        try {
            UserId userId = new UserId(command.userId());
            if (profileRepository.existsByUserId(userId)) {
                return Result.failure(new ProfileError("error.profile.already-exists"));
            }

            Profile profile = Profile.register(
                    userId,
                    new FundoName(command.fundoName()),
                    new ContactPhone(command.contactPhone()),
                    new MoistureThreshold(command.moistureThreshold()),
                    new TemperatureThreshold(command.temperatureThreshold())
            );

            return Result.success(toResult(profileRepository.save(profile)));
        } catch (IllegalArgumentException exception) {
            return Result.failure(new ProfileError(exception.getMessage()));
        }
    }

    /**
     * Replaces the editable data of an existing profile.
     *
     * @param command update command
     * @return the updated profile or an application error
     */
    @Transactional
    public Result<ProfileResult, ProfileError> update(UpdateProfileCommand command) {
        if (command == null) {
            return Result.failure(new ProfileError("error.command.required"));
        }

        try {
            UserId userId = new UserId(command.userId());
            Profile profile = profileRepository.findByUserId(userId).orElse(null);
            if (profile == null) {
                return Result.failure(new ProfileError("error.profile.not-found"));
            }

            profile.changeFundoName(new FundoName(command.fundoName()));
            profile.changeContactPhone(new ContactPhone(command.contactPhone()));
            profile.changeMoistureThreshold(new MoistureThreshold(command.moistureThreshold()));
            profile.changeTemperatureThreshold(new TemperatureThreshold(command.temperatureThreshold()));

            return Result.success(toResult(profileRepository.save(profile)));
        } catch (IllegalArgumentException exception) {
            return Result.failure(new ProfileError(exception.getMessage()));
        }
    }

    private ProfileResult toResult(Profile profile) {
        Long id = profile.id()
                .map(ProfileId::value)
                .orElseThrow(() -> new IllegalArgumentException("error.profile.id.not-assigned"));

        return new ProfileResult(
                id,
                profile.userId().value(),
                profile.fundoName().value(),
                profile.contactPhone().value(),
                profile.moistureThreshold().value(),
                profile.temperatureThreshold().value()
        );
    }
}
