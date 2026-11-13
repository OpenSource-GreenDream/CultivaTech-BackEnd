package greendream.cultivatech.platform.profile.application.commandservices;

import greendream.cultivatech.platform.profile.application.commands.CreateFieldCommand;
import greendream.cultivatech.platform.profile.application.commands.DeleteFieldCommand;
import greendream.cultivatech.platform.profile.application.commands.UpdateFieldCommand;
import greendream.cultivatech.platform.profile.application.results.FieldError;
import greendream.cultivatech.platform.profile.application.results.FieldResult;
import greendream.cultivatech.platform.shared.application.result.Result;

/**
 * Application service contract for field write use cases.
 */
public interface FieldCommandService {

    /**
     * Creates a field owned by an existing profile.
     *
     * @param command creation command
     * @return the persisted field or an application error
     */
    Result<FieldResult, FieldError> create(CreateFieldCommand command);

    /**
     * Replaces the editable data of an existing field.
     *
     * @param command update command
     * @return the updated field or an application error
     */
    Result<FieldResult, FieldError> update(UpdateFieldCommand command);

    /**
     * Deletes an existing field.
     *
     * @param command delete command
     * @return an empty success or an application error
     */
    Result<Void, FieldError> delete(DeleteFieldCommand command);
}
