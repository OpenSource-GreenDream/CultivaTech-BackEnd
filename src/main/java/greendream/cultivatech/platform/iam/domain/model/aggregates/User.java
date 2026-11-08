package greendream.cultivatech.platform.iam.domain.model.aggregates;

import greendream.cultivatech.platform.iam.domain.model.events.UserRegisteredEvent;
import greendream.cultivatech.platform.iam.domain.model.valueobjects.EmailAddress;
import greendream.cultivatech.platform.iam.domain.model.valueobjects.PasswordHash;
import greendream.cultivatech.platform.iam.domain.model.valueobjects.RoleId;
import greendream.cultivatech.platform.iam.domain.model.valueobjects.UserId;
import greendream.cultivatech.platform.shared.domain.aggregates.AbstractDomainAggregateRoot;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * User aggregate root.
 * Identity, credentials and role assignments only. Cross-context
 * relationships are coordinated by the application layer through UserId.
 */
public final class User extends AbstractDomainAggregateRoot<User> {

    private UserId id;
    private EmailAddress email;
    private PasswordHash passwordHash;
    private final Set<RoleId> roleIds;
    private boolean active;

    private User(UserId id,
                 EmailAddress email,
                 PasswordHash passwordHash,
                 Set<RoleId> roleIds,
                 boolean active) {
        this.id = id;
        this.email = Objects.requireNonNull(email, "email must not be null");
        this.passwordHash = Objects.requireNonNull(passwordHash, "passwordHash must not be null");
        this.roleIds = new LinkedHashSet<>(Objects.requireNonNull(roleIds, "roleIds must not be null"));
        this.active = active;
    }

    /**
     * Creates a new user that has not been persisted yet.
     */
    public static User register(EmailAddress email, PasswordHash passwordHash) {
        User user = new User(null, email, passwordHash, Set.of(), true);
        user.registerDomainEvent(new UserRegisteredEvent(email));
        return user;
    }

    /**
     * Rebuilds a persisted user.
     */
    public static User reconstitute(UserId id,
                                    EmailAddress email,
                                    PasswordHash passwordHash,
                                    Set<RoleId> roleIds,
                                    boolean active) {
        return new User(
                Objects.requireNonNull(id, "id must not be null"),
                email,
                passwordHash,
                roleIds,
                active
        );
    }

    public Optional<UserId> id() {
        return Optional.ofNullable(id);
    }

    public EmailAddress email() {
        return email;
    }

    public PasswordHash passwordHash() {
        return passwordHash;
    }

    public Set<RoleId> roleIds() {
        return Collections.unmodifiableSet(roleIds);
    }

    public boolean isActive() {
        return active;
    }

    /**
     * Assigns a role. Assigning an already assigned role is idempotent.
     */
    public void assignRole(RoleId roleId) {
        Objects.requireNonNull(roleId, "roleId must not be null");
        roleIds.add(roleId);
    }

    /**
     * Revokes a role. Revoking a role the user does not have is a no-op.
     */
    public void revokeRole(RoleId roleId) {
        Objects.requireNonNull(roleId, "roleId must not be null");
        roleIds.remove(roleId);
    }

    public boolean hasRole(RoleId roleId) {
        return roleIds.contains(roleId);
    }

    public void changeEmail(EmailAddress newEmail) {
        this.email = Objects.requireNonNull(newEmail, "email must not be null");
    }

    public void changePassword(PasswordHash newPasswordHash) {
        this.passwordHash = Objects.requireNonNull(newPasswordHash, "passwordHash must not be null");
    }

    public void activate() {
        this.active = true;
    }

    public void deactivate() {
        this.active = false;
    }
}
