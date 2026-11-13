package greendream.cultivatech.platform.notification.infrastructure.persistence.jpa.repositories;

import greendream.cultivatech.platform.notification.application.services.NotificationErrors;
import greendream.cultivatech.platform.notification.domain.model.aggregates.Notification;
import greendream.cultivatech.platform.notification.domain.repositories.NotificationRepository;
import greendream.cultivatech.platform.notification.infrastructure.persistence.jpa.entities.NotificationEntity;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/** Adapter: implements the domain port using Spring Data JPA. */
@Repository
public class NotificationRepositoryImpl implements NotificationRepository {
    private final NotificationJpaRepository jpa;

    public NotificationRepositoryImpl(NotificationJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public List<Notification> findAll() {
        return jpa.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public List<Notification> findAllByProfileId(Long profileId) {
        return jpa.findAllByProfileIdOrderByCreatedAtDesc(profileId).stream().map(this::toDomain).toList();
    }

    @Override
    public Notification save(Notification notification) {
        NotificationEntity entity = new NotificationEntity();
        apply(entity, notification);
        entity.setProfileId(notification.getProfileId());
        return toDomain(jpa.save(entity));
    }

    @Override
    @Transactional
    public Notification update(Notification notification) {
        NotificationEntity entity = jpa.findById(notification.getId())
                .orElseThrow(() -> new IllegalArgumentException(NotificationErrors.NOT_FOUND));
        apply(entity, notification);
        return toDomain(jpa.saveAndFlush(entity));
    }

    @Override
    public Optional<Notification> findById(Long id) {
        return jpa.findById(id).map(this::toDomain);
    }

    @Override
    public void deleteById(Long id) {
        jpa.deleteById(id);
    }

    @Override
    public void deleteAll() {
        jpa.deleteAll();
    }

    private void apply(NotificationEntity entity, Notification n) {
        entity.setTitle(n.getTitle());
        entity.setMessage(n.getMessage());
        entity.setIsRead(n.isRead());
        entity.setIsAlert(n.isAlert());
    }

    private Notification toDomain(NotificationEntity e) {
        return Notification.restore(e.getId(), e.getProfileId(), e.getTitle(), e.getMessage(),
                Boolean.TRUE.equals(e.getIsRead()), Boolean.TRUE.equals(e.getIsAlert()),
                e.getCreatedAt(), e.getUpdatedAt());
    }
}