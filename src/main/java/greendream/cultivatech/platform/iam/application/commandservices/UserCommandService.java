package greendream.cultivatech.platform.iam.application.commandservices;

import greendream.cultivatech.platform.iam.application.commands.DeleteUserCommand;
import greendream.cultivatech.platform.iam.application.commands.UpdateUserCommand;
import greendream.cultivatech.platform.iam.application.results.UserError;
import greendream.cultivatech.platform.iam.application.results.UserResult;
import greendream.cultivatech.platform.shared.application.result.Result;

/**
 * Application service contract for user write use cases.
 */
public interface UserCommandService {

    /**
     * Updates the email, optional password and optional roles of a user.
     *
     * @param command update command
     * @return the updated user or an application error
     */
    Result<UserResult, UserError> update(UpdateUserCommand command);

    /**
     * Deletes a user.
     *
     * @param command delete command
     * @return an empty success or an application error
     */
    Result<Void, UserError> delete(DeleteUserCommand command);
}
