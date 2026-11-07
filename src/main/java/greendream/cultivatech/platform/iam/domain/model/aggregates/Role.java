package greendream.cultivatech.platform.iam.domain.model.aggregates;

import greendream.cultivatech.platform.iam.domain.model.valueobjects.RoleId;
import greendream.cultivatech.platform.iam.domain.model.valueobjects.RoleName;
import greendream.cultivatech.platform.shared.domain.aggregates.AbstractDomainAggregateRoot;

import java.util.Objects;
import java.util.Optional;

/**
 * Role aggregate root.
 * The name is immutable and unique. A rename is modeled as a new role, not as a
 * mutation of an existing one.
 */
public final class Role extends AbstractDomainAggregateRoot<Role> {

    private RoleId id;
    private final RoleName name;
    private String description;

    private Role(RoleId id, RoleName name, String description) {
        this.id = id;
        this.name = Objects.requireNonNull(name, "name must not be null");
        this.description = normalizeDescription(description);
    }

    /**
     * Creates a new role that has not been persisted yet.
     */
    public static Role create(RoleName name, String description) {
        return new Role(null, name, description);
    }

    /**
     * Rebuilds a persisted role.
     */
    public static Role reconstitute(RoleId id, RoleName name, String description) {
        return new Role(Objects.requireNonNull(id, "id must not be null"), name, description);
    }

    public Optional<RoleId> id() {
        return Optional.ofNullable(id);
    }

    public RoleName name() {
        return name;
    }

    public String description() {
        return description;
    }

    public void updateDescription(String description) {
        this.description = normalizeDescription(description);
    }

    private static String normalizeDescription(String description) {
        if (description == null || description.isBlank()) {
            return null;
        }
        return description.trim();
    }
}
