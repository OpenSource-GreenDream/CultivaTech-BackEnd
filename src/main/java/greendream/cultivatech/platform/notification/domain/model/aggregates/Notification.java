package greendream.cultivatech.platform.notification.domain.model.aggregates;

import greendream.cultivatech.platform.shared.domain.aggregates.AbstractDomainAggregateRoot;

import java.util.Date;

public class Notification extends AbstractDomainAggregateRoot<Notification> {
    private Long id;
    private final Long profileId;
    private String title;
    private String message;
    private boolean read;
    private boolean alert;
    private Date createdAt;
    private Date updatedAt;

    /** Creates a NEW notification (unread, not persisted yet). */
    public Notification(Long profileId, String title, String message, boolean alert) {
        if (profileId == null) throw new IllegalArgumentException("error.notification.profile-id.required");
        validateContent(title, message);
        this.profileId = profileId;
        this.title = title.trim();
        this.message = message.trim();
        this.alert = alert;
        this.read = false;
    }

    /** Rebuilds an existing notification coming from persistence. */
    public static Notification restore(Long id, Long profileId, String title, String message,
                                       boolean read, boolean alert, Date createdAt, Date updatedAt) {
        Notification n = new Notification(profileId, title, message, alert);
        n.id = id;
        n.read = read;
        n.createdAt = createdAt;
        n.updatedAt = updatedAt;
        return n;
    }

    public void update(String title, String message, boolean alert) {
        validateContent(title, message);
        this.title = title.trim();
        this.message = message.trim();
        this.alert = alert;
    }

    public void markAsRead() {
        this.read = true;
    }

    private static void validateContent(String title, String message) {
        if (title == null || title.isBlank()) throw new IllegalArgumentException("error.notification.title.required");
        if (title.length() > 255) throw new IllegalArgumentException("error.notification.title.too-long");
        if (message == null || message.isBlank()) throw new IllegalArgumentException("error.notification.message.required");
        if (message.length() > 1000) throw new IllegalArgumentException("error.notification.message.too-long");
    }

    public Long getId() { return id; }
    public Long getProfileId() { return profileId; }
    public String getTitle() { return title; }
    public String getMessage() { return message; }
    public boolean isRead() { return read; }
    public boolean isAlert() { return alert; }
    public Date getCreatedAt() { return createdAt; }
    public Date getUpdatedAt() { return updatedAt; }
}
