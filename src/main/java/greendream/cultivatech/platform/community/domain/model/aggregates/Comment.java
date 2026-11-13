package greendream.cultivatech.platform.community.domain.model.aggregates;

import greendream.cultivatech.platform.shared.domain.aggregates.AbstractDomainAggregateRoot;

public class Comment extends AbstractDomainAggregateRoot<Comment> {
    private Long id;
    private final Long authorProfileId;
    private final String targetType;
    private final Long targetProfileId;
    private final Long fieldId;
    private final Long productId;
    private String content;
    private int rating;

    public Comment(Long authorProfileId, String targetType, Long targetProfileId, Long fieldId,
                   Long productId, String content, int rating) {
        if (authorProfileId == null || authorProfileId <= 0) throw new IllegalArgumentException("error.community.author-profile-id.invalid");
        if (targetProfileId == null || targetProfileId <= 0) throw new IllegalArgumentException("error.community.target-profile-id.invalid");
        this.targetType = targetType == null ? "PROFILE" : targetType.trim().toUpperCase();
        if (!this.targetType.equals("PROFILE") && !this.targetType.equals("FIELD") && !this.targetType.equals("PRODUCT"))
            throw new IllegalArgumentException("error.community.target-type.invalid");
        if ((this.targetType.equals("FIELD") && fieldId == null) || (this.targetType.equals("PRODUCT") && productId == null))
            throw new IllegalArgumentException("error.community.target-id.required");
        if (content == null || content.isBlank() || content.length() > 1000) throw new IllegalArgumentException("error.community.comment.invalid");
        if (rating < 1 || rating > 5) throw new IllegalArgumentException("error.community.rating.invalid");
        this.authorProfileId = authorProfileId;
        this.targetProfileId = targetProfileId;
        this.fieldId = fieldId;
        this.productId = productId;
        this.content = content.trim();
        this.rating = rating;
    }

    public static Comment restore(Long id, Long author, String type, Long target, Long field, Long product, String content, int rating) {
        Comment comment = new Comment(author, type, target, field, product, content, rating);
        comment.id = id;
        return comment;
    }

    public void update(String content, int rating) {
        if (content == null || content.isBlank() || content.length() > 1000) throw new IllegalArgumentException("error.community.comment.invalid");
        if (rating < 1 || rating > 5) throw new IllegalArgumentException("error.community.rating.invalid");
        this.content = content.trim();
        this.rating = rating;
    }

    public Long getId() { return id; }
    public Long getAuthorProfileId() { return authorProfileId; }
    public String getTargetType() { return targetType; }
    public Long getTargetProfileId() { return targetProfileId; }
    public Long getFieldId() { return fieldId; }
    public Long getProductId() { return productId; }
    public String getContent() { return content; }
    public int getRating() { return rating; }
}
