package greendream.cultivatech.platform.iam.application.querieservices;

import greendream.cultivatech.platform.iam.application.queries.GetAllRolesQuery;
import greendream.cultivatech.platform.iam.application.queries.GetRoleByIdQuery;
import greendream.cultivatech.platform.iam.application.results.RoleError;
import greendream.cultivatech.platform.iam.application.results.RoleResult;
import greendream.cultivatech.platform.shared.application.result.Result;

import java.util.List;

/**
 * Application service contract for role read use cases.
 */
public interface RoleQueryService {

    /**
     * Lists every IAM role.
     *
     * @param query list query
     * @return the roles or an application error
     */
    Result<List<RoleResult>, RoleError> getAllRoles(GetAllRolesQuery query);

    /**
     * Retrieves one IAM role.
     *
     * @param query lookup query
     * @return the role or an application error
     */
    Result<RoleResult, RoleError> getRoleById(GetRoleByIdQuery query);
}
