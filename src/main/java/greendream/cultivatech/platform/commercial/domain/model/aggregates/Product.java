package greendream.cultivatech.platform.commercial.domain.model.aggregates;

import java.math.BigDecimal;

/** A product offered in the agricultural catalog. Inventory remains owned by its context. */
public record Product(Long id, Long inventoryId, Long fieldId, String name,
                      String description, BigDecimal price, String imageUrl) {
    public Product {
        if (inventoryId == null || inventoryId <= 0) throw new IllegalArgumentException("Inventory id is required");
        if (fieldId != null && fieldId <= 0) throw new IllegalArgumentException("Field id must be positive");
        if (name == null || name.isBlank() || name.length() > 255) throw new IllegalArgumentException("Product name is required (max 255 characters)");
        if (description != null && description.length() > 1000) throw new IllegalArgumentException("Description must not exceed 1000 characters");
        if (price == null || price.signum() < 0 || price.scale() > 2 || price.precision() > 18) throw new IllegalArgumentException("Price must be non-negative with at most two decimals");
        if (imageUrl != null && imageUrl.length() > 500) throw new IllegalArgumentException("Image URL must not exceed 500 characters");
        name = name.trim();
        description = description == null ? null : description.trim();
        imageUrl = imageUrl == null || imageUrl.isBlank() ? null : imageUrl.trim();
    }
}
