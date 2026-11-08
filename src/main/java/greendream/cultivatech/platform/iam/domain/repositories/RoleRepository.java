package greendream.cultivatech.platform.iam.domain.repositories;

import greendream.cultivatech.platform.iam.domain.model.aggregates.Role;
import greendream.cultivatech.platform.iam.domain.model.valueobjects.RoleId;
import greendream.cultivatech.platform.iam.domain.model.valueobjects.RoleName;

import java.util.Optional;

/**
 * Persistence contract for role aggregate.
 */
public interface RoleRepository {

    Role save(Role role);

    Optional<Role> findById(RoleId id);

    Optional<Role> findByName(RoleName name);

    boolean existsByName(RoleName name);
}
