package greendream.cultivatech.platform.notification.application.commands;

public record UpdateNotificationCommand(Long id, String title, String message, boolean isAlert) {
}