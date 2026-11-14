package greendream.cultivatech.platform.community.infrastructure.persistence.jpa.repositories;

import greendream.cultivatech.platform.community.infrastructure.persistence.jpa.entities.CommunityProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommunityProfileJpaRepository extends JpaRepository<CommunityProfileEntity, Long> {
    Optional<CommunityProfileEntity> findByProfileId(Long profileId);
}
