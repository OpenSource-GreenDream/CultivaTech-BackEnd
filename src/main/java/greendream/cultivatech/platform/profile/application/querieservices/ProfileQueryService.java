package greendream.cultivatech.platform.profile.application.querieservices;

import greendream.cultivatech.platform.profile.application.queries.GetProfileByUserIdQuery;
import greendream.cultivatech.platform.profile.application.results.ProfileError;
import greendream.cultivatech.platform.profile.application.results.ProfileResult;
import greendream.cultivatech.platform.shared.application.result.Result;

/**
 * Application service contract for profile read use cases.
 */
public interface ProfileQueryService {

    /**
     * Retrieves the profile owned by the user referenced by the query.
     *
     * @param query lookup query
     * @return the profile or an application error
     */
    Result<ProfileResult, ProfileError> findByUserId(GetProfileByUserIdQuery query);
}
