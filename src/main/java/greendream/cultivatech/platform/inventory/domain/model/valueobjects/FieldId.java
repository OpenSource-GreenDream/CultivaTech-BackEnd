package greendream.cultivatech.platform.inventory.domain.model.valueobjects;

/**
 * Reference to the field where a stock row is stored.
 */
public record FieldId(Long value) {

    public FieldId {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("error.inventory.field-id.invalid");
        }
    }
}
