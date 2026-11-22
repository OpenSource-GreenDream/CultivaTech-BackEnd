package greendream.cultivatech.platform.commercial.infrastructure.persistence.jpa.repositories;

import greendream.cultivatech.platform.commercial.infrastructure.persistence.jpa.entities.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrderJpaRepository extends JpaRepository<OrderEntity, Long> {
    List<OrderEntity> findAllByProfileIdOrderByCreatedAtDesc(Long profileId);
}
