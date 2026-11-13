package greendream.cultivatech.platform.iam.domain.repositories;

import greendream.cultivatech.platform.iam.domain.model.aggregates.User;
import greendream.cultivatech.platform.iam.domain.model.valueobjects.EmailAddress;
import greendream.cultivatech.platform.iam.domain.model.valueobjects.UserId;
import greendream.cultivatech.platform.shared.domain.repositories.ICrudService;

import java.util.Optional;

/**
 * Persistence contract for the user aggregate.
 */
public interface UserRepository extends ICrudService<User, UserId> {

    Optional<User> findByEmail(EmailAddress email);

    boolean existsByEmail(EmailAddress email);
}
