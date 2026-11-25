package greendream.cultivatech.platform.community.domain.model.aggregates;

import greendream.cultivatech.platform.shared.domain.aggregates.AbstractDomainAggregateRoot;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

public class CommunityProfile extends AbstractDomainAggregateRoot<CommunityProfile> {
    private Long id;
    private final Long profileId;
    private String nickname;
    private BigDecimal reputationScore;
    private String publicBio;
    private int visibilityStatus;
    private Date createdAt;
    private Date updatedAt;

    public CommunityProfile(Long profileId, String nickname, int reputationScore, String publicBio, int visibilityStatus) {
        this(profileId, nickname, BigDecimal.valueOf(reputationScore), publicBio, visibilityStatus);
    }

    public CommunityProfile(Long profileId, String nickname, BigDecimal reputationScore, String publicBio, int visibilityStatus) {
        if (profileId == null || profileId <= 0) throw new IllegalArgumentException("error.community.profile-id.invalid");
        validate(nickname, publicBio);
        this.profileId = profileId;
        this.nickname = nickname.trim();
        this.reputationScore = normalize(reputationScore == null ? BigDecimal.ZERO : reputationScore);
        this.publicBio = publicBio == null ? null : publicBio.trim();
        this.visibilityStatus = visibilityStatus;
    }

    public static CommunityProfile restore(Long id, Long profileId, String nickname, int reputationScore,
                                           String publicBio, int visibilityStatus, Date createdAt, Date updatedAt) {
        return restore(id, profileId, nickname, BigDecimal.valueOf(reputationScore), publicBio, visibilityStatus, createdAt, updatedAt);
    }

    public static CommunityProfile restore(Long id, Long profileId, String nickname, BigDecimal reputationScore,
                                           String publicBio, int visibilityStatus, Date createdAt, Date updatedAt) {
        CommunityProfile profile = new CommunityProfile(profileId, nickname, reputationScore, publicBio, visibilityStatus);
        profile.id = id;
        profile.createdAt = createdAt;
        profile.updatedAt = updatedAt;
        return profile;
    }

    public void update(String nickname, int reputationScore, String publicBio, int visibilityStatus) {
        update(nickname, BigDecimal.valueOf(reputationScore), publicBio, visibilityStatus);
    }

    public void update(String nickname, BigDecimal reputationScore, String publicBio, int visibilityStatus) {
        validate(nickname, publicBio);
        this.nickname = nickname.trim();
        this.reputationScore = normalize(reputationScore == null ? this.reputationScore : reputationScore);
        this.publicBio = publicBio == null ? null : publicBio.trim();
        this.visibilityStatus = visibilityStatus;
    }

    public void updateReputationScore(BigDecimal reputationScore) {
        this.reputationScore = normalize(reputationScore == null ? BigDecimal.ZERO : reputationScore);
    }

    private static void validate(String nickname, String bio) {
        if (nickname == null || nickname.isBlank() || nickname.length() > 100) throw new IllegalArgumentException("error.community.nickname.invalid");
        if (bio != null && bio.length() > 500) throw new IllegalArgumentException("error.community.bio.too-long");
    }

    private static BigDecimal normalize(BigDecimal value) {
        BigDecimal rounded = value.setScale(2, java.math.RoundingMode.HALF_UP);
        if (rounded.compareTo(BigDecimal.ZERO) < 0) rounded = BigDecimal.ZERO;
        if (rounded.compareTo(new BigDecimal("5.00")) > 0) rounded = new BigDecimal("5.00");
        return rounded;
    }

    public Long getId() { return id; }
    public Long getProfileId() { return profileId; }
    public String getNickname() { return nickname; }
    public BigDecimal getReputationScore() { return reputationScore; }
    public String getPublicBio() { return publicBio; }
    public int getVisibilityStatus() { return visibilityStatus; }
    public Date getCreatedAt() { return createdAt; }
    public Date getUpdatedAt() { return updatedAt; }
}