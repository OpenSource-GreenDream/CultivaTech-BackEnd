package greendream.cultivatech.platform.iam.application.commandservices;

import greendream.cultivatech.platform.iam.application.commands.CreateRoleCommand;
import greendream.cultivatech.platform.iam.application.commands.DeleteRoleCommand;
import greendream.cultivatech.platform.iam.application.commands.UpdateRoleCommand;
import greendream.cultivatech.platform.iam.application.results.RoleError;
import greendream.cultivatech.platform.iam.application.results.RoleResult;
import greendream.cultivatech.platform.shared.application.result.Result;

/**
 * Application service contract for role write use cases.
 */
public interface RoleCommandService {

    /**
     * Creates a role.
     *
     * @param command creation command
     * @return the persisted role or an application error
     */
    Result<RoleResult, RoleError> create(CreateRoleCommand command);

    /**
     * Updates the description of a role.
     *
     * @param command update command
     * @return the updated role or an application error
     */
    Result<RoleResult, RoleError> update(UpdateRoleCommand command);

    /**
     * Deletes a role that is not assigned to any user.
     *
     * @param command delete command
     * @return an empty success or an application error
     */
    Result<Void, RoleError> delete(DeleteRoleCommand command);
}
