package greendream.cultivatech.platform.profile.application.querieservices;

import greendream.cultivatech.platform.profile.application.queries.GetProfileByUserIdQuery;
import greendream.cultivatech.platform.profile.application.results.ProfileError;
import greendream.cultivatech.platform.profile.application.results.ProfileResult;
import greendream.cultivatech.platform.profile.domain.model.aggregates.Profile;
import greendream.cultivatech.platform.profile.domain.model.valueobjects.ProfileId;
import greendream.cultivatech.platform.profile.domain.model.valueobjects.UserId;
import greendream.cultivatech.platform.profile.domain.repositories.ProfileRepository;
import greendream.cultivatech.platform.shared.application.result.Result;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Application service for profile read use cases.
 */
@Service
public class ProfileQueryService {

    private final ProfileRepository profileRepository;

    public ProfileQueryService(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    /**
     * Retrieves the profile owned by the user referenced by the query.
     *
     * @param query lookup query
     * @return the profile or an application error
     */
    @Transactional(readOnly = true)
    public Result<ProfileResult, ProfileError> findByUserId(GetProfileByUserIdQuery query) {
        if (query == null) {
            return Result.failure(new ProfileError("error.command.required"));
        }

        try {
            UserId userId = new UserId(query.userId());
            Optional<Profile> profile = profileRepository.findByUserId(userId);
            if (profile.isEmpty()) {
                return Result.failure(new ProfileError("error.profile.not-found"));
            }

            return Result.success(toResult(profile.get()));
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
