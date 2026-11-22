package greendream.cultivatech.platform.commercial.interfaces.rest;

import greendream.cultivatech.platform.commercial.application.CommercialService;
import greendream.cultivatech.platform.commercial.domain.model.aggregates.Order;
import greendream.cultivatech.platform.commercial.domain.model.aggregates.Product;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping(value="/api/v1", produces=MediaType.APPLICATION_JSON_VALUE)
@Tag(name="Commercial", description="Agricultural product catalog and customer orders")
public class CommercialController {
    private final CommercialService commercial;
    public CommercialController(CommercialService commercial) { this.commercial = commercial; }

    @GetMapping("/products") public List<Product> products() { return commercial.getProducts(); }
    @GetMapping("/products/{id}") public Product product(@PathVariable Long id) { return commercial.getProduct(id); }
    @PostMapping(value="/products", consumes=MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Product> createProduct(@Valid @RequestBody ProductRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(commercial.createProduct(request.inventoryId(), request.fieldId(), request.name(), request.description(), request.price(), request.imageUrl()));
    }
    @PutMapping(value="/products/{id}", consumes=MediaType.APPLICATION_JSON_VALUE)
    public Product updateProduct(@PathVariable Long id, @Valid @RequestBody ProductRequest request) {
        return commercial.updateProduct(id, request.inventoryId(), request.fieldId(), request.name(), request.description(), request.price(), request.imageUrl());
    }
    @DeleteMapping("/products/{id}") public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        commercial.deleteProduct(id); return ResponseEntity.noContent().build();
    }
    @GetMapping("/orders") public List<Order> orders(@RequestParam(required=false) Long profileId) { return commercial.getOrders(profileId); }
    @GetMapping("/orders/{id}") public Order order(@PathVariable Long id) { return commercial.getOrder(id); }
    @PostMapping(value="/orders", consumes=MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Order> createOrder(@Valid @RequestBody OrderRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(commercial.createOrder(request.profileId(), request.productId(), request.quantity(), request.paymentMethod(), Boolean.TRUE.equals(request.subscription())));
    }
    @PatchMapping(value="/orders/{id}/status", consumes=MediaType.APPLICATION_JSON_VALUE)
    public Order updateOrderStatus(@PathVariable Long id, @Valid @RequestBody OrderStatusRequest request) {
        return commercial.updateOrderStatus(id, request.status());
    }

    public record ProductRequest(@NotNull @Positive Long inventoryId, @Positive Long fieldId,
                                 @NotBlank @Size(max=255) String name, @Size(max=1000) String description,
                                 @NotNull @DecimalMin("0.00") @Digits(integer=16, fraction=2) BigDecimal price,
                                 @Size(max=500) String imageUrl) { }
    public record OrderRequest(@NotNull @Positive Long profileId, @NotNull @Positive Long productId,
                               @Positive int quantity, @PositiveOrZero int paymentMethod, Boolean subscription) { }
    public record OrderStatusRequest(@PositiveOrZero int status) { }
}
