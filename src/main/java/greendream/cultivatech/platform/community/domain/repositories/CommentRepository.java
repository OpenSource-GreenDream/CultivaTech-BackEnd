package greendream.cultivatech.platform.community.domain.repositories;

import greendream.cultivatech.platform.community.domain.model.aggregates.Comment;
import greendream.cultivatech.platform.shared.domain.repositories.ICrudService;

import java.util.List;

public interface CommentRepository extends ICrudService<Comment, Long> {
    List<Comment> findByTargetProfileId(Long profileId);
    List<Comment> findByFieldId(Long fieldId);
    List<Comment> findByProductId(Long productId);
}
