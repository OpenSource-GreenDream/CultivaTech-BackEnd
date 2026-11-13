package greendream.cultivatech.platform.community.infrastructure.persistence.jpa.entities;

import greendream.cultivatech.platform.shared.infrastructure.persistence.jpa.AuditableAbstractPersistenceEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor
@Entity @Table(name = "comments")
public class CommentEntity extends AuditableAbstractPersistenceEntity {
    @Column(name = "author_profile_id", nullable = false) private Long authorProfileId;
    @Column(name = "target_type", nullable = false, length = 20) private String targetType;
    @Column(name = "target_profile_id") private Long targetProfileId;
    @Column(name = "field_id") private Long fieldId;
    @Column(name = "product_id") private Long productId;
    @Column(name = "content", nullable = false, length = 1000) private String content;
    @Column(name = "rating") private Integer rating;
}
