package greendream.cultivatech.platform.notification.infrastructure.persistence.jpa.repositories;

import greendream.cultivatech.platform.notification.infrastructure.persistence.jpa.entities.NotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationJpaRepository extends JpaRepository<NotificationEntity, Long> {
    List<NotificationEntity> findAllByProfileIdOrderByCreatedAtDesc(Long profileId);
}