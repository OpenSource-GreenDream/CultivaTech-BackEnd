package greendream.cultivatech.platform.profile.domain.model.events;

import greendream.cultivatech.platform.profile.domain.model.valueobjects.UserId;
import greendream.cultivatech.platform.shared.domain.events.DomainEvents;

import java.time.Instant;
import java.util.Objects;

/**
 * Raised when a new profile is registered.
 *
 * <p>The owning user reference is known before the profile is persisted, so it is part
 * of the event. Consumers that need the profile identity can resolve it by user id.</p>
 */
public record ProfileRegisteredEvent(UserId userId, Instant occurredAt) implements DomainEvents {

    public ProfileRegisteredEvent {
        Objects.requireNonNull(userId, "userId must not be null");
        Objects.requireNonNull(occurredAt, "occurredAt must not be null");
    }

    public ProfileRegisteredEvent(UserId userId) {
        this(userId, Instant.now());
    }
}
