package greendream.cultivatech.platform.community.domain.model.aggregates;

import greendream.cultivatech.platform.shared.domain.aggregates.AbstractDomainAggregateRoot;

import java.math.BigDecimal;
import java.net.URI;
import java.util.Date;
import java.util.Objects;

public class CommunityProfile extends AbstractDomainAggregateRoot<CommunityProfile> {
    private Long id;
    private final Long profileId;
    private String nickname;
    private BigDecimal reputationScore;
    private String publicBio;
    private int visibilityStatus;
    private String imageUrl;
    private Date createdAt;
    private Date updatedAt;

    public CommunityProfile(Long profileId, String nickname, int reputationScore, String publicBio, int visibilityStatus) {
        this(profileId, nickname, BigDecimal.valueOf(reputationScore), publicBio, visibilityStatus, null);
    }

    public CommunityProfile(Long profileId, String nickname, BigDecimal reputationScore, String publicBio, int visibilityStatus) {
        this(profileId, nickname, reputationScore, publicBio, visibilityStatus, null);
    }

    public CommunityProfile(Long profileId, String nickname, int reputationScore, String publicBio, int visibilityStatus, String imageUrl) {
        this(profileId, nickname, BigDecimal.valueOf(reputationScore), publicBio, visibilityStatus, imageUrl);
    }

    public CommunityProfile(Long profileId, String nickname, BigDecimal reputationScore, String publicBio, int visibilityStatus, String imageUrl) {
        if (profileId == null || profileId <= 0) throw new IllegalArgumentException("error.community.profile-id.invalid");
        validate(nickname, publicBio, imageUrl);
        this.profileId = profileId;
        this.nickname = nickname.trim();
        this.reputationScore = normalize(reputationScore == null ? BigDecimal.ZERO : reputationScore);
        this.publicBio = publicBio == null ? null : publicBio.trim();
        this.visibilityStatus = visibilityStatus;
        this.imageUrl = normalizeImageUrl(imageUrl);
    }

    public static CommunityProfile restore(Long id, Long profileId, String nickname, int reputationScore,
                                           String publicBio, int visibilityStatus, Date createdAt, Date updatedAt) {
        return restore(id, profileId, nickname, BigDecimal.valueOf(reputationScore), publicBio, visibilityStatus, createdAt, updatedAt, null);
    }

    public static CommunityProfile restore(Long id, Long profileId, String nickname, BigDecimal reputationScore,
                                           String publicBio, int visibilityStatus, Date createdAt, Date updatedAt) {
        return restore(id, profileId, nickname, reputationScore, publicBio, visibilityStatus, createdAt, updatedAt, null);
    }

    public static CommunityProfile restore(Long id, Long profileId, String nickname, int reputationScore,
                                           String publicBio, int visibilityStatus, Date createdAt, Date updatedAt, String imageUrl) {
        return restore(id, profileId, nickname, BigDecimal.valueOf(reputationScore), publicBio, visibilityStatus, createdAt, updatedAt, imageUrl);
    }

    public static CommunityProfile restore(Long id, Long profileId, String nickname, BigDecimal reputationScore,
                                           String publicBio, int visibilityStatus, Date createdAt, Date updatedAt, String imageUrl) {
        CommunityProfile profile = new CommunityProfile(profileId, nickname, reputationScore, publicBio, visibilityStatus, imageUrl);
        profile.id = id;
        profile.createdAt = createdAt;
        profile.updatedAt = updatedAt;
        return profile;
    }

    public void update(String nickname, int reputationScore, String publicBio, int visibilityStatus) {
        update(nickname, BigDecimal.valueOf(reputationScore), publicBio, visibilityStatus);
    }

    public void update(String nickname, BigDecimal reputationScore, String publicBio, int visibilityStatus) {
        update(nickname, reputationScore, publicBio, visibilityStatus, this.imageUrl);
    }

    public void update(String nickname, int reputationScore, String publicBio, int visibilityStatus, String imageUrl) {
        update(nickname, BigDecimal.valueOf(reputationScore), publicBio, visibilityStatus, imageUrl);
    }

    public void update(String nickname, BigDecimal reputationScore, String publicBio, int visibilityStatus, String imageUrl) {
        validate(nickname, publicBio, imageUrl);
        this.nickname = nickname.trim();
        this.reputationScore = normalize(reputationScore == null ? this.reputationScore : reputationScore);
        this.publicBio = publicBio == null ? null : publicBio.trim();
        this.visibilityStatus = visibilityStatus;
        this.imageUrl = normalizeImageUrl(imageUrl);
    }

    public void updateReputationScore(BigDecimal reputationScore) {
        this.reputationScore = normalize(reputationScore == null ? BigDecimal.ZERO : reputationScore);
    }

    private static void validate(String nickname, String bio) {
        validate(nickname, bio, null);
    }

    private static void validate(String nickname, String bio, String imageUrl) {
        if (nickname == null || nickname.isBlank() || nickname.length() > 100) throw new IllegalArgumentException("error.community.nickname.invalid");
        if (bio != null && bio.length() > 500) throw new IllegalArgumentException("error.community.bio.too-long");
        if (imageUrl != null && !imageUrl.isBlank()) {
            String trimmed = imageUrl.trim();
            if (trimmed.length() > 2048) throw new IllegalArgumentException("error.community.image-url.too-long");
            if (!isAbsoluteHttpUrl(trimmed)) throw new IllegalArgumentException("error.community.image-url.invalid");
        }
    }

    private static String normalizeImageUrl(String imageUrl) {
        if (imageUrl == null || imageUrl.isBlank()) return null;
        return imageUrl.trim();
    }

    private static boolean isAbsoluteHttpUrl(String url) {
        try {
            URI uri = URI.create(url);
            String scheme = uri.getScheme();
            return scheme != null
                    && (scheme.equalsIgnoreCase("http") || scheme.equalsIgnoreCase("https"))
                    && uri.getHost() != null
                    && !uri.getHost().isBlank();
        } catch (Exception e) {
            return false;
        }
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
    public String getImageUrl() { return imageUrl; }
    public Date getCreatedAt() { return createdAt; }
    public Date getUpdatedAt() { return updatedAt; }
}