package greendream.cultivatech.platform.inventory.application.commandservices;

import greendream.cultivatech.platform.inventory.application.commands.DeleteInventoryCommand;
import greendream.cultivatech.platform.inventory.application.commands.RegisterInventoryCommand;
import greendream.cultivatech.platform.inventory.application.commands.UpdateInventoryCommand;
import greendream.cultivatech.platform.inventory.application.results.InventoryError;
import greendream.cultivatech.platform.inventory.application.results.InventoryResult;
import greendream.cultivatech.platform.shared.application.result.Result;

/**
 * Application service contract for inventory write use cases.
 */
public interface InventoryCommandService {

    /**
     * Registers an inventory entry for an existing profile.
     *
     * @param command registration command
     * @return the persisted entry or an application error
     */
    Result<InventoryResult, InventoryError> register(RegisterInventoryCommand command);

    /**
     * Updates the editable data of an existing inventory entry.
     *
     * @param command update command
     * @return the updated entry or an application error
     */
    Result<InventoryResult, InventoryError> update(UpdateInventoryCommand command);

    /**
     * Deletes an existing inventory entry.
     *
     * @param command delete command
     * @return an empty success or an application error
     */
    Result<Void, InventoryError> delete(DeleteInventoryCommand command);
}
