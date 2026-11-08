package greendream.cultivatech.platform.profile.domain.repositories;

import greendream.cultivatech.platform.profile.domain.model.aggregates.Profile;
import greendream.cultivatech.platform.profile.domain.model.valueobjects.ProfileId;
import greendream.cultivatech.platform.profile.domain.model.valueobjects.UserId;

import java.util.Optional;

/**
 * Persistence contract for the profile aggregate.
 */
public interface ProfileRepository {

    Profile save(Profile profile);

    Optional<Profile> findById(ProfileId id);

    Optional<Profile> findByUserId(UserId userId);

    boolean existsByUserId(UserId userId);
}
