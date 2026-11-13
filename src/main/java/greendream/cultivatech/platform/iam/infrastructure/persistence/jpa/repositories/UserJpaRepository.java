package greendream.cultivatech.platform.iam.infrastructure.persistence.jpa.repositories;

import greendream.cultivatech.platform.iam.infrastructure.persistence.jpa.entities.UserPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Spring Data repository for the users table.
 */
public interface UserJpaRepository extends JpaRepository<UserPersistenceEntity, Long> {

    Optional<UserPersistenceEntity> findByEmailAddress(String emailAddress);

    boolean existsByEmailAddress(String emailAddress);

    boolean existsByRoles_Id(Long roleId);
}
