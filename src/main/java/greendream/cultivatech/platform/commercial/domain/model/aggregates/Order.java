package greendream.cultivatech.platform.commercial.domain.model.aggregates;

import java.math.BigDecimal;

/** A purchase request with a price snapshot that does not change with the catalog. */
public record Order(Long id, Long profileId, Long productId, String productName, int quantity,
                    BigDecimal totalAmount, int status, int paymentMethod, boolean subscription) {
    public Order {
        if (profileId == null || profileId <= 0) throw new IllegalArgumentException("Profile id must be positive");
        if (productId == null || productId <= 0) throw new IllegalArgumentException("Product id must be positive");
        if (productName == null || productName.isBlank() || productName.length() > 255) throw new IllegalArgumentException("Product name is required");
        if (quantity <= 0) throw new IllegalArgumentException("Quantity must be positive");
        if (totalAmount == null || totalAmount.signum() < 0 || totalAmount.scale() > 2) throw new IllegalArgumentException("Order total is invalid");
        if (status < 0 || paymentMethod < 0) throw new IllegalArgumentException("Status and payment method must be non-negative");
    }
}
