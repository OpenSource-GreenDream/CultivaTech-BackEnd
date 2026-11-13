package greendream.cultivatech.platform.iam.domain.repositories;

import greendream.cultivatech.platform.iam.domain.model.aggregates.Role;
import greendream.cultivatech.platform.iam.domain.model.valueobjects.RoleId;
import greendream.cultivatech.platform.iam.domain.model.valueobjects.RoleName;
import greendream.cultivatech.platform.shared.domain.repositories.ICrudService;

import java.util.Optional;

/**
 * Persistence contract for role aggregate.
 */
public interface RoleRepository extends ICrudService<Role, RoleId> {

    Optional<Role> findByName(RoleName name);

    boolean existsByName(RoleName name);
}
