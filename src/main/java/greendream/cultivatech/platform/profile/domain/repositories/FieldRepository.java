package greendream.cultivatech.platform.profile.domain.repositories;

import greendream.cultivatech.platform.profile.domain.model.aggregates.Field;
import greendream.cultivatech.platform.profile.domain.model.valueobjects.FieldId;
import greendream.cultivatech.platform.profile.domain.model.valueobjects.ProfileId;
import greendream.cultivatech.platform.shared.domain.repositories.ICrudService;

import java.util.List;

/**
 * Persistence contract for the field aggregate.
 *
 * <p>Extends the shared CRUD service contract. In addition to the CRUD operations it
 * exposes the queries the Profile context needs: listing the fields owned by a
 * profile and checking whether a profile has any field.</p>
 */
public interface FieldRepository extends ICrudService<Field, FieldId> {

    List<Field> findByProfileId(ProfileId profileId);

    boolean existsByProfileId(ProfileId profileId);
}
