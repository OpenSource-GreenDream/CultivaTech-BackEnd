package greendream.cultivatech.platform.community.infrastructure.persistence.jpa.repositories;

import greendream.cultivatech.platform.community.infrastructure.persistence.jpa.entities.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface CommentJpaRepository extends JpaRepository<CommentEntity, Long> {
    List<CommentEntity> findAllByTargetProfileId(Long profileId);
    List<CommentEntity> findAllByFieldId(Long fieldId);
    List<CommentEntity> findAllByProductId(Long productId);

    @Query("select avg(c.rating) from CommentEntity c where c.targetProfileId = :targetProfileId and c.targetType = :targetType")
    Double findAverageRatingByTargetProfileIdAndTargetType(@Param("targetProfileId") Long targetProfileId,
                                                         @Param("targetType") String targetType);
}
