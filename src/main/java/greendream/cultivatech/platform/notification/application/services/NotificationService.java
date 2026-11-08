package greendream.cultivatech.platform.notification.application.services;

import greendream.cultivatech.platform.notification.application.commands.CreateNotificationCommand;
import greendream.cultivatech.platform.notification.application.commands.UpdateNotificationCommand;
import greendream.cultivatech.platform.notification.domain.model.aggregates.Notification;
import greendream.cultivatech.platform.notification.domain.repositories.NotificationRepository;
import greendream.cultivatech.platform.shared.application.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class NotificationService {
    private final NotificationRepository repository;

    public NotificationService(NotificationRepository repository) {
        this.repository = repository;
    }

    public Result<Notification, String> create(CreateNotificationCommand command) {
        // TODO: Replace hardcoded profileId handling with ProfileContext integration (validate that the profile exists)
        try {
            Notification notification = new Notification(
                    command.profileId(), command.title(), command.message(), command.isAlert());
            return Result.success(repository.save(notification));
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            return internalError("create", e);
        }
    }

    public Result<List<Notification>, String> findAll() {
        try {
            return Result.success(repository.findAll());
        } catch (Exception e) {
            return internalError("findAll", e);
        }
    }

    public Result<List<Notification>, String> findAllByProfileId(Long profileId) {
        try {
            return Result.success(repository.findAllByProfileId(profileId));
        } catch (Exception e) {
            return internalError("findAllByProfileId", e);
        }
    }

    public Result<Notification, String> findById(Long id) {
        try {
            return repository.findById(id)
                    .<Result<Notification, String>>map(Result::success)
                    .orElseGet(() -> Result.failure(NotificationErrors.NOT_FOUND));
        } catch (Exception e) {
            return internalError("findById", e);
        }
    }

    public Result<Notification, String> update(UpdateNotificationCommand command) {
        try {
            var found = repository.findById(command.id());
            if (found.isEmpty()) return Result.failure(NotificationErrors.NOT_FOUND);
            Notification notification = found.get();
            notification.update(command.title(), command.message(), command.isAlert());
            return Result.success(repository.update(notification));
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            return internalError("update", e);
        }
    }

    public Result<Notification, String> markAsRead(Long id) {
        try {
            var found = repository.findById(id);
            if (found.isEmpty()) return Result.failure(NotificationErrors.NOT_FOUND);
            Notification notification = found.get();
            notification.markAsRead();
            return Result.success(repository.update(notification));
        } catch (Exception e) {
            return internalError("markAsRead", e);
        }
    }

    public Result<Void, String> delete(Long id) {
        try {
            if (repository.findById(id).isEmpty()) return Result.failure(NotificationErrors.NOT_FOUND);
            repository.deleteById(id);
            return Result.success(null);
        } catch (Exception e) {
            return internalError("delete", e);
        }
    }

    private <T> Result<T, String> internalError(String operation, Exception e) {
        log.error("Notification {} failed", operation, e);
        return Result.failure(NotificationErrors.INTERNAL);
    }
}
