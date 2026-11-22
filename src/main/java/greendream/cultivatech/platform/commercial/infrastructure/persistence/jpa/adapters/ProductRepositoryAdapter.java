package greendream.cultivatech.platform.commercial.infrastructure.persistence.jpa.adapters;

import greendream.cultivatech.platform.commercial.domain.model.aggregates.Product;
import greendream.cultivatech.platform.commercial.domain.repositories.ProductRepository;
import greendream.cultivatech.platform.commercial.infrastructure.persistence.jpa.entities.ProductEntity;
import greendream.cultivatech.platform.commercial.infrastructure.persistence.jpa.repositories.ProductJpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/** JPA adapter translating product rows to the Commercial domain model. */
@Repository
public class ProductRepositoryAdapter implements ProductRepository {
    private final ProductJpaRepository jpa;
    public ProductRepositoryAdapter(ProductJpaRepository jpa) { this.jpa = jpa; }

    @Override public List<Product> findAll() { return jpa.findAll().stream().map(this::toDomain).toList(); }
    @Override public Optional<Product> findById(Long id) { return jpa.findById(id).map(this::toDomain); }
    @Override public Optional<Product> findByInventoryId(Long id) { return jpa.findByInventoryId(id).map(this::toDomain); }
    @Override public Product save(Product product) {
        ProductEntity entity = product.id() == null ? new ProductEntity() : jpa.findById(product.id()).orElseGet(ProductEntity::new);
        entity.setInventoryId(product.inventoryId()); entity.setFieldId(product.fieldId()); entity.setName(product.name());
        entity.setDescription(product.description()); entity.setPrice(product.price()); entity.setImageUrl(product.imageUrl());
        entity.setType(product.type());
        return toDomain(jpa.save(entity));
    }
    @Override public void deleteById(Long id) { jpa.deleteById(id); }

    private Product toDomain(ProductEntity entity) {
        return new Product(entity.getId(), entity.getInventoryId(), entity.getFieldId(), entity.getName(),
                entity.getDescription(), entity.getPrice(), entity.getType(), entity.getImageUrl());
    }
}
