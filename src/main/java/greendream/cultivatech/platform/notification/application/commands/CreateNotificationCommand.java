package greendream.cultivatech.platform.notification.application.commands;

public record CreateNotificationCommand(Long profileId, String title,String message, boolean isAlert) {
}