package greendream.cultivatech.platform.inventory.application.querieservices;

import greendream.cultivatech.platform.inventory.application.queries.GetAllInventoriesQuery;
import greendream.cultivatech.platform.inventory.application.queries.GetInventoriesByFieldIdQuery;
import greendream.cultivatech.platform.inventory.application.queries.GetInventoriesByProfileIdQuery;
import greendream.cultivatech.platform.inventory.application.queries.GetInventoryByIdQuery;
import greendream.cultivatech.platform.inventory.application.results.InventoryError;
import greendream.cultivatech.platform.inventory.application.results.InventoryResult;
import greendream.cultivatech.platform.shared.application.result.Result;

import java.util.List;

/**
 * Application service contract for inventory read use cases.
 */
public interface InventoryQueryService {

    /**
     * Lists every inventory entry.
     *
     * @param query list query
     * @return the entries or an application error
     */
    Result<List<InventoryResult>, InventoryError> getAllInventories(GetAllInventoriesQuery query);

    /**
     * Retrieves one inventory entry.
     *
     * @param query lookup query
     * @return the entry or an application error
     */
    Result<InventoryResult, InventoryError> getById(GetInventoryByIdQuery query);

    /**
     * Lists the inventory entries owned by a profile.
     *
     * @param query lookup query
     * @return the entries or an application error
     */
    Result<List<InventoryResult>, InventoryError> getByProfileId(GetInventoriesByProfileIdQuery query);

    /**
     * Lists the inventory entries stored on a field.
     *
     * @param query lookup query
     * @return the entries or an application error
     */
    Result<List<InventoryResult>, InventoryError> getByFieldId(GetInventoriesByFieldIdQuery query);
}
