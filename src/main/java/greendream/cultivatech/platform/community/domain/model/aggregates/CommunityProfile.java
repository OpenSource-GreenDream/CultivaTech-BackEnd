package greendream.cultivatech.platform.community.domain.model.aggregates;

import greendream.cultivatech.platform.shared.domain.aggregates.AbstractDomainAggregateRoot;
import java.util.Date;

public class CommunityProfile extends AbstractDomainAggregateRoot<CommunityProfile> {
    private Long id;
    private final Long profileId;
    private String nickname;
    private int reputationScore;
    private String publicBio;
    private int visibilityStatus;
    private Date createdAt;
    private Date updatedAt;

    public CommunityProfile(Long profileId, String nickname, int reputationScore, String publicBio, int visibilityStatus) {
        if (profileId == null || profileId <= 0) throw new IllegalArgumentException("error.community.profile-id.invalid");
        validate(nickname, publicBio, reputationScore);
        this.profileId = profileId;
        this.nickname = nickname.trim();
        this.reputationScore = reputationScore;
        this.publicBio = publicBio == null ? null : publicBio.trim();
        this.visibilityStatus = visibilityStatus;
    }

    public static CommunityProfile restore(Long id, Long profileId, String nickname, int reputationScore,
                                           String publicBio, int visibilityStatus, Date createdAt, Date updatedAt) {
        CommunityProfile profile = new CommunityProfile(profileId, nickname, reputationScore, publicBio, visibilityStatus);
        profile.id = id;
        profile.createdAt = createdAt;
        profile.updatedAt = updatedAt;
        return profile;
    }

    public void update(String nickname, int reputationScore, String publicBio, int visibilityStatus) {
        validate(nickname, publicBio, reputationScore);
        this.nickname = nickname.trim();
        this.reputationScore = reputationScore;
        this.publicBio = publicBio == null ? null : publicBio.trim();
        this.visibilityStatus = visibilityStatus;
    }

    private static void validate(String nickname, String bio, int score) {
        if (nickname == null || nickname.isBlank() || nickname.length() > 100) throw new IllegalArgumentException("error.community.nickname.invalid");
        if (bio != null && bio.length() > 500) throw new IllegalArgumentException("error.community.bio.too-long");
        if (score < 0) throw new IllegalArgumentException("error.community.reputation-score.invalid");
    }

    public Long getId() { return id; }
    public Long getProfileId() { return profileId; }
    public String getNickname() { return nickname; }
    public int getReputationScore() { return reputationScore; }
    public String getPublicBio() { return publicBio; }
    public int getVisibilityStatus() { return visibilityStatus; }
    public Date getCreatedAt() { return createdAt; }
    public Date getUpdatedAt() { return updatedAt; }
}
