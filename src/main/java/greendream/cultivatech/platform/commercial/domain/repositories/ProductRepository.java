package greendream.cultivatech.platform.commercial.domain.repositories;

import greendream.cultivatech.platform.commercial.domain.model.aggregates.Product;
import java.util.List;
import java.util.Optional;

/** Persistence port for the Commercial product aggregate. */
public interface ProductRepository {
    List<Product> findAll();
    Optional<Product> findById(Long id);
    Optional<Product> findByInventoryId(Long inventoryId);
    Product save(Product product);
    void deleteById(Long id);
}
