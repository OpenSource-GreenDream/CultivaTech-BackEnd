package greendream.cultivatech.platform.inventory.domain.model.valueobjects;

/**
 * Number of product units available in an inventory row.
 */
public record StockQuantity(Integer value) {

    public StockQuantity {
        if (value == null || value < 0) {
            throw new IllegalArgumentException("error.inventory.stock-quantity.invalid");
        }
    }
}
