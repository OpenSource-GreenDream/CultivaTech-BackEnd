package greendream.cultivatech.platform.community.infrastructure.persistence.jpa.repositories;

import greendream.cultivatech.platform.community.infrastructure.persistence.jpa.entities.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentJpaRepository extends JpaRepository<CommentEntity, Long> {
    List<CommentEntity> findAllByTargetProfileId(Long profileId);
    List<CommentEntity> findAllByFieldId(Long fieldId);
    List<CommentEntity> findAllByProductId(Long productId);
}
