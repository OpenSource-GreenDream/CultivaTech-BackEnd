package greendream.cultivatech.platform.notification.interfaces.rest;

import greendream.cultivatech.platform.notification.application.services.NotificationErrors;
import greendream.cultivatech.platform.notification.application.services.NotificationService;
import greendream.cultivatech.platform.notification.domain.model.aggregates.Notification;
import greendream.cultivatech.platform.notification.interfaces.rest.resources.CreateNotificationResource;
import greendream.cultivatech.platform.notification.interfaces.rest.resources.UpdateNotificationResource;
import greendream.cultivatech.platform.notification.interfaces.rest.transform.NotificationResourceAssembler;
import greendream.cultivatech.platform.shared.application.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/notifications", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Notifications", description = "Notification management endpoints")
public class NotificationsController {
    private final NotificationService service;

    public NotificationsController(NotificationService service) {
        this.service = service;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Create a notification")
    public ResponseEntity<?> create(@Valid @RequestBody CreateNotificationResource resource) {
        return service.create(NotificationResourceAssembler.toCommand(resource))
                .<ResponseEntity<?>>fold(
                        n -> ResponseEntity.status(HttpStatus.CREATED).body(NotificationResourceAssembler.toResource(n)),
                        this::error);
    }

    @GetMapping
    @Operation(summary = "List notifications (optionally filtered by profileId)")
    public ResponseEntity<?> getAll(@RequestParam(required = false) Long profileId) {
        Result<List<Notification>, String> result =
                profileId == null ? service.findAll() : service.findAllByProfileId(profileId);
        return result.<ResponseEntity<?>>fold(
                list -> ResponseEntity.ok(list.stream().map(NotificationResourceAssembler::toResource).toList()),
                this::error);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a notification by id")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return service.findById(id).<ResponseEntity<?>>fold(
                n -> ResponseEntity.ok(NotificationResourceAssembler.toResource(n)), this::error);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Update a notification")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody UpdateNotificationResource resource) {
        return service.update(NotificationResourceAssembler.toCommand(id, resource))
                .<ResponseEntity<?>>fold(
                        n -> ResponseEntity.ok(NotificationResourceAssembler.toResource(n)), this::error);
    }

    @PatchMapping("/{id}/read")
    @Operation(summary = "Mark a notification as read")
    public ResponseEntity<?> markAsRead(@PathVariable Long id) {
        return service.markAsRead(id).<ResponseEntity<?>>fold(
                n -> ResponseEntity.ok(NotificationResourceAssembler.toResource(n)), this::error);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a notification")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        return service.delete(id).<ResponseEntity<?>>fold(
                ignored -> ResponseEntity.noContent().build(), this::error);
    }

    private ResponseEntity<?> error(String errorKey) {
        HttpStatus status = NotificationErrors.NOT_FOUND.equals(errorKey)
                ? HttpStatus.NOT_FOUND : HttpStatus.INTERNAL_SERVER_ERROR;
        return ResponseEntity.status(status).body(ProblemDetail.forStatusAndDetail(status, errorKey));
    }
}