package greendream.cultivatech.platform.notification.interfaces.rest.transform;

import greendream.cultivatech.platform.notification.application.commands.CreateNotificationCommand;
import greendream.cultivatech.platform.notification.application.commands.UpdateNotificationCommand;
import greendream.cultivatech.platform.notification.domain.model.aggregates.Notification;
import greendream.cultivatech.platform.notification.interfaces.rest.resources.CreateNotificationResource;
import greendream.cultivatech.platform.notification.interfaces.rest.resources.NotificationResource;
import greendream.cultivatech.platform.notification.interfaces.rest.resources.UpdateNotificationResource;

public final class NotificationResourceAssembler {
    private NotificationResourceAssembler() {
    }

    public static CreateNotificationCommand toCommand(CreateNotificationResource r) {
        return new CreateNotificationCommand(r.profileId(), r.title(), r.message(), Boolean.TRUE.equals(r.isAlert()));
    }

    public static UpdateNotificationCommand toCommand(Long id, UpdateNotificationResource r) {
        return new UpdateNotificationCommand(id, r.title(), r.message(), Boolean.TRUE.equals(r.isAlert()));
    }

    public static NotificationResource toResource(Notification n) {
        return new NotificationResource(n.getId(), n.getProfileId(), n.getTitle(), n.getMessage(),
                n.isRead(), n.isAlert(), n.getCreatedAt(), n.getUpdatedAt());
    }
}