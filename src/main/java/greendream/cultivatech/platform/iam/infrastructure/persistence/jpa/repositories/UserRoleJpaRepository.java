package greendream.cultivatech.platform.iam.infrastructure.persistence.jpa.repositories;

import greendream.cultivatech.platform.iam.infrastructure.persistence.jpa.entities.UserRoleId;
import greendream.cultivatech.platform.iam.infrastructure.persistence.jpa.entities.UserRolePersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Spring Data repository for the user_roles join table.
 */
public interface UserRoleJpaRepository extends JpaRepository<UserRolePersistenceEntity, UserRoleId> {

    List<UserRolePersistenceEntity> findByUserId(Long userId);

    boolean existsByUserIdAndRoleId(Long userId, Long roleId);

    void deleteByUserId(Long userId);
}
