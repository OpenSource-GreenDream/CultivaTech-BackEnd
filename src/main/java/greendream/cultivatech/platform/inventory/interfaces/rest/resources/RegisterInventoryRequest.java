package greendream.cultivatech.platform.inventory.interfaces.rest.resources;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * HTTP request to register an inventory entry for a profile.
 */
public record RegisterInventoryRequest(
        @NotNull @Min(1) Long fieldId,
        @NotNull @Min(0) Integer stockQuantity
) {
}
