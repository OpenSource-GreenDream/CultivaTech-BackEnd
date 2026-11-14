package greendream.cultivatech.platform.notification.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateNotificationResource(
        @NotBlank @Size(max = 255) String title,
        @NotBlank @Size(max = 1000) String message,
        Boolean isAlert) {
}