package greendream.cultivatech.platform.notification.infrastructure.persistence.jpa.entities;

import greendream.cultivatech.platform.shared.infrastructure.persistence.jpa.AuditableAbstractPersistenceEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Maps to table "notifications" (see docs/database-diagram.md). */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "notifications")
public class NotificationEntity extends AuditableAbstractPersistenceEntity {

    // Plain column for now: the profiles table belongs to another bounded context.
    @Column(name = "profile_id", nullable = false)
    private Long profileId;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "message", nullable = false, length = 1000)
    private String message;

    @Column(name = "is_read", nullable = false)
    private Boolean isRead = false;

    @Column(name = "is_alert", nullable = false)
    private Boolean isAlert = false;
}