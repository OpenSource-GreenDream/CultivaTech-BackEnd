package greendream.cultivatech.platform.iam.application.querieservices;

import greendream.cultivatech.platform.iam.application.queries.GetAllUsersQuery;
import greendream.cultivatech.platform.iam.application.queries.GetUserByIdQuery;
import greendream.cultivatech.platform.iam.application.results.UserError;
import greendream.cultivatech.platform.iam.application.results.UserResult;
import greendream.cultivatech.platform.shared.application.result.Result;

import java.util.List;

/**
 * Application service contract for user read use cases.
 */
public interface UserQueryService {

    /**
     * Lists every IAM user.
     *
     * @param query list query
     * @return the users or an application error
     */
    Result<List<UserResult>, UserError> getAllUsers(GetAllUsersQuery query);

    /**
     * Retrieves one IAM user.
     *
     * @param query lookup query
     * @return the user or an application error
     */
    Result<UserResult, UserError> getUserById(GetUserByIdQuery query);
}
