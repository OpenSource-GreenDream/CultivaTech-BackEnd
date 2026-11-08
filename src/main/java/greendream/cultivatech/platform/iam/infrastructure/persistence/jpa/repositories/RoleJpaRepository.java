package greendream.cultivatech.platform.iam.infrastructure.persistence.jpa.repositories;

import greendream.cultivatech.platform.iam.infrastructure.persistence.jpa.entities.RolePersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Spring Data repository for the roles table.
 */
public interface RoleJpaRepository extends JpaRepository<RolePersistenceEntity, Long> {

    Optional<RolePersistenceEntity> findByName(String name);

    boolean existsByName(String name);
}
