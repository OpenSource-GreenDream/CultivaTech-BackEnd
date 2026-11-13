package greendream.cultivatech.platform.profile.application.querieservices;

import greendream.cultivatech.platform.profile.application.queries.GetFieldByIdQuery;
import greendream.cultivatech.platform.profile.application.queries.GetFieldsByProfileIdQuery;
import greendream.cultivatech.platform.profile.application.results.FieldError;
import greendream.cultivatech.platform.profile.application.results.FieldResult;
import greendream.cultivatech.platform.shared.application.result.Result;

import java.util.List;

/**
 * Application service contract for field read use cases.
 */
public interface FieldQueryService {

    /**
     * Retrieves a single field.
     *
     * @param query lookup query
     * @return the field or an application error
     */
    Result<FieldResult, FieldError> getById(GetFieldByIdQuery query);

    /**
     * Lists every field owned by a profile. A profile without fields yields an empty
     * list, not an error.
     *
     * @param query list query
     * @return the fields or an application error
     */
    Result<List<FieldResult>, FieldError> getByProfileId(GetFieldsByProfileIdQuery query);
}
