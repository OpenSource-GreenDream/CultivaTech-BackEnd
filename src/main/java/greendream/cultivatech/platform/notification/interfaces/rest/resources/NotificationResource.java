package greendream.cultivatech.platform.notification.interfaces.rest.resources;

import java.util.Date;

public record NotificationResource(
        Long id,
        Long profileId,
        String title,
        String message,
        boolean isRead,
        boolean isAlert,
        Date createdAt,
        Date updatedAt) {
}