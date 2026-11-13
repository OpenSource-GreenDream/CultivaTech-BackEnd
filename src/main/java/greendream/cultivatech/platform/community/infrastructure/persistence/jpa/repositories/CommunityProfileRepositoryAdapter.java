package greendream.cultivatech.platform.community.infrastructure.persistence.jpa.repositories;

import greendream.cultivatech.platform.community.domain.model.aggregates.CommunityProfile;
import greendream.cultivatech.platform.community.domain.repositories.CommunityProfileRepository;
import greendream.cultivatech.platform.community.infrastructure.persistence.jpa.entities.CommunityProfileEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class CommunityProfileRepositoryAdapter implements CommunityProfileRepository {
    private final CommunityProfileJpaRepository jpa;
    public CommunityProfileRepositoryAdapter(CommunityProfileJpaRepository jpa) { this.jpa = jpa; }
    @Override public List<CommunityProfile> findAll() { return jpa.findAll().stream().map(this::toDomain).toList(); }
    @Override public Optional<CommunityProfile> findById(Long id) { return jpa.findById(id).map(this::toDomain); }
    @Override public Optional<CommunityProfile> findByProfileId(Long id) { return jpa.findByProfileId(id).map(this::toDomain); }
    @Override public CommunityProfile save(CommunityProfile p) { return toDomain(jpa.save(toEntity(p))); }
    @Override public CommunityProfile update(CommunityProfile p) {
        CommunityProfileEntity entity = jpa.findById(p.getId()).orElseThrow();
        entity.setNickname(p.getNickname()); entity.setReputationScore(p.getReputationScore());
        entity.setPublicBio(p.getPublicBio()); entity.setVisibilityStatus(p.getVisibilityStatus());
        return toDomain(jpa.save(entity));
    }
    @Override public void deleteById(Long id) { jpa.deleteById(id); }
    @Override public void deleteAll() { jpa.deleteAll(); }
    private CommunityProfileEntity toEntity(CommunityProfile p) {
        CommunityProfileEntity e = new CommunityProfileEntity(); e.setProfileId(p.getProfileId());
        e.setNickname(p.getNickname()); e.setReputationScore(p.getReputationScore());
        e.setPublicBio(p.getPublicBio()); e.setVisibilityStatus(p.getVisibilityStatus()); return e;
    }
    private CommunityProfile toDomain(CommunityProfileEntity e) {
        return CommunityProfile.restore(e.getId(), e.getProfileId(), e.getNickname(), e.getReputationScore(), e.getPublicBio(), e.getVisibilityStatus());
    }
}
