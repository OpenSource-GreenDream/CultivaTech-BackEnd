package greendream.cultivatech.platform.community.domain.model.aggregates;

import greendream.cultivatech.platform.shared.domain.aggregates.AbstractDomainAggregateRoot;

public class CommunityProfile extends AbstractDomainAggregateRoot<CommunityProfile> {
    private Long id;
    private final Long profileId;
    private String nickname;
    private int reputationScore;
    private String publicBio;
    private int visibilityStatus;

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
                                           String publicBio, int visibilityStatus) {
        CommunityProfile profile = new CommunityProfile(profileId, nickname, reputationScore, publicBio, visibilityStatus);
        profile.id = id;
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
}
