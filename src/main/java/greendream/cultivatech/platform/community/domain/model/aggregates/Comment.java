package greendream.cultivatech.platform.community.domain.model.aggregates;

import greendream.cultivatech.platform.shared.domain.aggregates.AbstractDomainAggregateRoot;

import java.util.Date;
import java.util.Objects;
import java.util.Set;

public class Comment extends AbstractDomainAggregateRoot<Comment> {
    private static final Set<String> VALID_TARGET_TYPES = Set.of("PROFILE", "FIELD", "PRODUCT");
    private static final int MAX_CONTENT_LENGTH = 1000;

    private Long id;
    private final Long authorProfileId;
    private final String targetType;
    private final Long targetProfileId;
    private final Long fieldId;
    private final Long productId;
    private String content;
    private int rating;
    private Date createdAt;
    private Date updatedAt;

    public Comment(Long authorProfileId,
                   String targetType,
                   Long targetProfileId,
                   Long fieldId,
                   Long productId,
                   String content,
                   int rating) {
        if (authorProfileId == null || authorProfileId <= 0) {
            throw new IllegalArgumentException("error.community.author-profile-id.invalid");
        }
        String normalizedType = targetType == null ? null : targetType.trim().toUpperCase();
        if (normalizedType == null || !VALID_TARGET_TYPES.contains(normalizedType)) {
            throw new IllegalArgumentException("error.community.target-type.invalid");
        }
        if (targetProfileId == null || targetProfileId <= 0) {
            throw new IllegalArgumentException("error.community.target-profile-id.invalid");
        }
        if (content == null || content.isBlank() || content.length() > MAX_CONTENT_LENGTH) {
            throw new IllegalArgumentException("error.community.comment.invalid");
        }
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("error.community.rating.invalid");
        }
        validateConditionalTargets(normalizedType, fieldId, productId);

        this.authorProfileId = authorProfileId;
        this.targetType = normalizedType;
        this.targetProfileId = targetProfileId;
        this.fieldId = fieldId;
        this.productId = productId;
        this.content = content.trim();
        this.rating = rating;
    }

    public static Comment restore(Long id,
                                 Long authorProfileId,
                                 String targetType,
                                 Long targetProfileId,
                                 Long fieldId,
                                 Long productId,
                                 String content,
                                 int rating,
                                 Date createdAt,
                                 Date updatedAt) {
        Comment comment = new Comment(authorProfileId, targetType, targetProfileId, fieldId, productId, content, rating);
        comment.id = id;
        comment.createdAt = createdAt;
        comment.updatedAt = updatedAt;
        return comment;
    }

    public void update(String content, int rating) {
        if (content == null || content.isBlank() || content.length() > MAX_CONTENT_LENGTH) {
            throw new IllegalArgumentException("error.community.comment.invalid");
        }
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("error.community.rating.invalid");
        }
        this.content = content.trim();
        this.rating = rating;
    }

    private static void validateConditionalTargets(String targetType, Long fieldId, Long productId) {
        switch (targetType) {
            case "PROFILE" -> {
                if (fieldId != null || productId != null) {
                    throw new IllegalArgumentException("error.community.target-inconsistent");
                }
            }
            case "FIELD" -> {
                if (fieldId == null || fieldId <= 0 || productId != null) {
                    throw new IllegalArgumentException("error.community.target-inconsistent");
                }
            }
            case "PRODUCT" -> {
                if (productId == null || productId <= 0 || fieldId != null) {
                    throw new IllegalArgumentException("error.community.target-inconsistent");
                }
            }
            default -> throw new IllegalArgumentException("error.community.target-type.invalid");
        }
    }

    public Long getId() { return id; }
    public Long getAuthorProfileId() { return authorProfileId; }
    public String getTargetType() { return targetType; }
    public Long getTargetProfileId() { return targetProfileId; }
    public Long getFieldId() { return fieldId; }
    public Long getProductId() { return productId; }
    public String getContent() { return content; }
    public int getRating() { return rating; }
    public Date getCreatedAt() { return createdAt; }
    public Date getUpdatedAt() { return updatedAt; }
}