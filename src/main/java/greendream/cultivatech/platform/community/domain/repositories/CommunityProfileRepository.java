package greendream.cultivatech.platform.community.domain.repositories;

import greendream.cultivatech.platform.community.domain.model.aggregates.CommunityProfile;
import greendream.cultivatech.platform.shared.domain.repositories.ICrudService;

import java.util.Optional;

public interface CommunityProfileRepository extends ICrudService<CommunityProfile, Long> {
    Optional<CommunityProfile> findByProfileId(Long profileId);
}
