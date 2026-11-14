package greendream.cultivatech.platform.inventory.interfaces.rest.resources;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * HTTP request to update the editable data of an inventory entry.
 */
public record UpdateInventoryRequest(
        @NotNull @Min(1) Long fieldId,
        @NotNull @Min(0) Integer stockQuantity
) {
}
