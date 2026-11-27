package greendream.cultivatech.platform.community.application.services;

import greendream.cultivatech.platform.community.domain.model.aggregates.Comment;
import greendream.cultivatech.platform.community.domain.model.aggregates.CommunityProfile;
import greendream.cultivatech.platform.community.domain.repositories.CommentRepository;
import greendream.cultivatech.platform.community.domain.repositories.CommunityProfileRepository;
import greendream.cultivatech.platform.iam.infrastructure.authorization.sfs.services.IamUserPrincipal;
import greendream.cultivatech.platform.profile.domain.model.aggregates.Field;
import greendream.cultivatech.platform.profile.domain.model.aggregates.Profile;
import greendream.cultivatech.platform.profile.domain.model.valueobjects.FieldId;
import greendream.cultivatech.platform.profile.domain.model.valueobjects.ProfileId;
import greendream.cultivatech.platform.profile.domain.model.valueobjects.UserId;
import greendream.cultivatech.platform.profile.domain.repositories.FieldRepository;
import greendream.cultivatech.platform.profile.domain.repositories.ProfileRepository;
import greendream.cultivatech.platform.commercial.domain.model.aggregates.Product;
import greendream.cultivatech.platform.commercial.domain.repositories.ProductRepository;
import greendream.cultivatech.platform.shared.domain.exceptions.ForbiddenException;
import greendream.cultivatech.platform.shared.domain.exceptions.NotFoundException;
import greendream.cultivatech.platform.shared.domain.exceptions.UnauthorizedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@Transactional
public class CommunityService {
    private final CommunityProfileRepository profiles;
    private final CommentRepository comments;
    private final ProfileRepository profileRepository;
    private final FieldRepository fieldRepository;
    private final ProductRepository productRepository;

    public CommunityService(CommunityProfileRepository profiles,
                            CommentRepository comments,
                            ProfileRepository profileRepository,
                            FieldRepository fieldRepository,
                            ProductRepository productRepository) {
        this.profiles = profiles;
        this.comments = comments;
        this.profileRepository = profileRepository;
        this.fieldRepository = fieldRepository;
        this.productRepository = productRepository;
    }

    @Transactional(readOnly = true)
    public List<CommunityProfile> findProfiles(Long profileId) {
        return profileId == null ? profiles.findAll() : profiles.findByProfileId(profileId).stream().toList();
    }

    @Transactional(readOnly = true)
    public CommunityProfile findProfile(Long id) {
        return profiles.findById(id).orElseThrow(() -> new NotFoundException("error.community.profile.not-found"));
    }

    public CommunityProfile createProfile(Long profileId, String nickname, Integer reputationScore, String publicBio, Integer visibility) {
        return createProfile(profileId, nickname, reputationScore, publicBio, visibility, null);
    }

    public CommunityProfile createProfile(Long profileId, String nickname, Integer reputationScore, String publicBio, Integer visibility, String imageUrl) {
        if (profiles.findByProfileId(profileId).isPresent()) {
            throw new greendream.cultivatech.platform.shared.domain.exceptions.ConflictException("error.community.profile.already-exists");
        }
        return profiles.save(new CommunityProfile(profileId, nickname, BigDecimal.ZERO, publicBio, visibility == null ? 0 : visibility, imageUrl));
    }

    public CommunityProfile updateProfile(Long id, String nickname, Integer reputationScore, String publicBio, Integer visibility) {
        CommunityProfile p = findProfile(id);
        p.update(nickname, p.getReputationScore(), publicBio, visibility == null ? p.getVisibilityStatus() : visibility);
        return profiles.update(p);
    }

    public CommunityProfile updateProfile(Long id, String nickname, Integer reputationScore, String publicBio, Integer visibility, String imageUrl) {
        CommunityProfile p = findProfile(id);
        p.update(nickname, p.getReputationScore(), publicBio, visibility == null ? p.getVisibilityStatus() : visibility, imageUrl);
        return profiles.update(p);
    }

    public void deleteProfile(Long id) {
        findProfile(id);
        profiles.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<Comment> findComments(Long targetProfileId, Long fieldId, Long productId) {
        if (targetProfileId != null) return comments.findByTargetProfileId(targetProfileId);
        if (fieldId != null) return comments.findByFieldId(fieldId);
        if (productId != null) return comments.findByProductId(productId);
        return comments.findAll();
    }

    @Transactional(readOnly = true)
    public Comment findComment(Long id) {
        return comments.findById(id).orElseThrow(() -> new NotFoundException("error.community.comment.not-found"));
    }

    public Comment createComment(Long authorProfileId, String type, Long targetProfileId, Long fieldId, Long productId, String content, Integer rating) {
        Long authenticatedProfileId = authenticatedProfileId();
        if (authorProfileId == null || !authorProfileId.equals(authenticatedProfileId)) {
            throw new IllegalArgumentException("error.community.author.invalid");
        }

        String targetType = type == null ? "PROFILE" : type.trim().toUpperCase();
        int ratingValue = rating == null ? 0 : rating;

        // Domain invariants (content, rating, conditional targets).
        Comment comment = new Comment(authenticatedProfileId, targetType, targetProfileId, fieldId, productId, content, ratingValue);

        // Auto-rating prevention and target validation.
        if ("PROFILE".equals(targetType)) {
            if (targetProfileId.equals(authenticatedProfileId)) {
                throw new IllegalArgumentException("error.community.self-rating");
            }
            profiles.findByProfileId(targetProfileId)
                    .orElseThrow(() -> new NotFoundException("error.community.profile.not-found"));
        } else if ("FIELD".equals(targetType)) {
            Field field = fieldRepository.findById(new FieldId(fieldId))
                    .orElseThrow(() -> new NotFoundException("error.profile.field.not-found"));
            if (!field.profileId().value().equals(targetProfileId)) {
                throw new IllegalArgumentException("error.community.target-inconsistent");
            }
            if (field.profileId().value().equals(authenticatedProfileId)) {
                throw new IllegalArgumentException("error.community.self-rating");
            }
        } else if ("PRODUCT".equals(targetType)) {
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new NotFoundException("errors.not-found"));
            if (product.fieldId() != null) {
                Field field = fieldRepository.findById(new FieldId(product.fieldId()))
                        .orElseThrow(() -> new NotFoundException("error.profile.field.not-found"));
                if (!field.profileId().value().equals(targetProfileId)) {
                    throw new IllegalArgumentException("error.community.target-inconsistent");
                }
                if (field.profileId().value().equals(authenticatedProfileId)) {
                    throw new IllegalArgumentException("error.community.self-rating");
                }
            } else {
                throw new IllegalArgumentException("error.community.target-inconsistent");
            }
        }

        Comment saved = comments.save(comment);

        if ("PROFILE".equals(targetType)) {
            recalculateReputation(targetProfileId);
        }
        return saved;
    }

    public Comment updateComment(Long id, String content, Integer rating) {
        Long authenticatedProfileId = authenticatedProfileId();
        Comment comment = findComment(id);
        if (!comment.getAuthorProfileId().equals(authenticatedProfileId)) {
            throw new ForbiddenException("error.community.not-author");
        }
        int oldRating = comment.getRating();
        int newRating = rating == null ? comment.getRating() : rating;
        comment.update(content == null ? comment.getContent() : content, newRating);

        Comment updated = comments.update(comment);

        if ("PROFILE".equals(comment.getTargetType()) && oldRating != updated.getRating()) {
            recalculateReputation(comment.getTargetProfileId());
        }
        return updated;
    }

    public void deleteComment(Long id) {
        Long authenticatedProfileId = authenticatedProfileId();
        Comment comment = findComment(id);
        if (!comment.getAuthorProfileId().equals(authenticatedProfileId)) {
            throw new ForbiddenException("error.community.not-author");
        }
        String targetType = comment.getTargetType();
        Long targetProfileId = comment.getTargetProfileId();
        comments.deleteById(comment.getId());

        if ("PROFILE".equals(targetType)) {
            recalculateReputation(targetProfileId);
        }
    }

    private Long authenticatedProfileId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || !(authentication.getPrincipal() instanceof IamUserPrincipal principal)) {
            throw new UnauthorizedException("error.authentication.unauthorized");
        }
        Profile profile = profileRepository.findByUserId(new UserId(principal.userId()))
                .orElseThrow(() -> new NotFoundException("error.profile.not-found"));
        return profile.id().orElseThrow(() -> new NotFoundException("error.profile.not-found")).value();
    }

    private void recalculateReputation(Long targetProfileId) {
        Double average = comments.findAverageRatingByTargetProfileIdAndTargetType(targetProfileId, "PROFILE");
        BigDecimal value = average == null ? BigDecimal.ZERO : BigDecimal.valueOf(average);
        value = value.setScale(2, RoundingMode.HALF_UP);

        CommunityProfile target = profiles.findByProfileId(targetProfileId)
                .orElseThrow(() -> new NotFoundException("error.community.profile.not-found"));
        target.updateReputationScore(value);
        profiles.update(target);
    }
}