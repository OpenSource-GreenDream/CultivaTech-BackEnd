package greendream.cultivatech.platform.community.infrastructure.persistence.jpa.entities;

import greendream.cultivatech.platform.shared.infrastructure.persistence.jpa.AuditableAbstractPersistenceEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor
@Entity @Table(name = "community_profiles")
public class CommunityProfileEntity extends AuditableAbstractPersistenceEntity {
    @Column(name = "profile_id", nullable = false, unique = true) private Long profileId;
    @Column(name = "nickname", nullable = false, length = 100) private String nickname;
    @Column(name = "reputation_score", nullable = false) private Integer reputationScore;
    @Column(name = "public_bio", length = 500) private String publicBio;
    @Column(name = "visibility_status", nullable = false) private Integer visibilityStatus;
}
