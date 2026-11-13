package greendream.cultivatech.platform.profile.domain.repositories;

import greendream.cultivatech.platform.profile.domain.model.aggregates.Profile;
import greendream.cultivatech.platform.profile.domain.model.valueobjects.ProfileId;
import greendream.cultivatech.platform.profile.domain.model.valueobjects.UserId;
import greendream.cultivatech.platform.shared.domain.repositories.ICrudService;

import java.util.List;
import java.util.Optional;

/**
 * Persistence contract for the profile aggregate.
 *
 * <p>Extends the shared CRUD service contract and redeclares its methods without
 * checked exceptions so the domain stays free of {@code throws Exception}.</p>
 */
public interface ProfileRepository extends ICrudService<Profile, ProfileId> {

    Optional<Profile> findByUserId(UserId userId);

    boolean existsByUserId(UserId userId);
}
