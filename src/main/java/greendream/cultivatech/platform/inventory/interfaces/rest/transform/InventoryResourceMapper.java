package greendream.cultivatech.platform.inventory.interfaces.rest.transform;

import greendream.cultivatech.platform.inventory.application.results.InventoryResult;
import greendream.cultivatech.platform.inventory.interfaces.rest.resources.InventoryResource;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Maps inventory application results to REST resources.
 */
@Component
public class InventoryResourceMapper {

    public InventoryResource toResource(InventoryResult result) {
        return new InventoryResource(
                result.id(),
                result.profileId(),
                result.fieldId(),
                result.stockQuantity()
        );
    }

    public List<InventoryResource> toResources(List<InventoryResult> results) {
        return results.stream()
                .map(this::toResource)
                .toList();
    }
}
