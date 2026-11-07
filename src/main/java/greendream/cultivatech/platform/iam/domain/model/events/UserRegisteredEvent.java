package greendream.cultivatech.platform.iam.domain.model.events;

import greendream.cultivatech.platform.iam.domain.model.valueobjects.EmailAddress;
import greendream.cultivatech.platform.shared.domain.events.DomainEvents;

import java.time.Instant;
import java.util.Objects;

/**
 * Raised when a new user is registered.
 * The persisted user identity is intentionally not part of this event: at
 * registration time the aggregate has not been persisted yet. Consumers that need
 * the identity can resolve the user by email.
 */
public record UserRegisteredEvent(EmailAddress email, Instant occurredAt) implements DomainEvents {

    public UserRegisteredEvent {
        Objects.requireNonNull(email, "email must not be null");
        Objects.requireNonNull(occurredAt, "occurredAt must not be null");
    }

    public UserRegisteredEvent(EmailAddress email) {
        this(email, Instant.now());
    }
}
