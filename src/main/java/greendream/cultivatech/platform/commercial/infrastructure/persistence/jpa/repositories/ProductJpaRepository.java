package greendream.cultivatech.platform.commercial.infrastructure.persistence.jpa.repositories;

import greendream.cultivatech.platform.commercial.infrastructure.persistence.jpa.entities.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ProductJpaRepository extends JpaRepository<ProductEntity, Long> {
    Optional<ProductEntity> findByInventoryId(Long inventoryId);
}
