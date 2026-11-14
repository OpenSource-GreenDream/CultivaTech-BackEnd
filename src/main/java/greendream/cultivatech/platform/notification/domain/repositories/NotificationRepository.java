package greendream.cultivatech.platform.notification.domain.repositories;

import greendream.cultivatech.platform.notification.domain.model.aggregates.Notification;
import greendream.cultivatech.platform.shared.domain.repositories.ICrudService;

import java.util.List;

/** Domain port for Notification persistence. Reuses the shared CRUD contract. */
public interface NotificationRepository extends ICrudService<Notification, Long> {
    List<Notification> findAllByProfileId(Long profileId) throws Exception;
}