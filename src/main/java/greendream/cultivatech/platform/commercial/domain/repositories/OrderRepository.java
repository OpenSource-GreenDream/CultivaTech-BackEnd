package greendream.cultivatech.platform.commercial.domain.repositories;

import greendream.cultivatech.platform.commercial.domain.model.aggregates.Order;
import java.util.List;
import java.util.Optional;

/** Persistence port for the Commercial order aggregate. */
public interface OrderRepository {
    List<Order> findAll();
    List<Order> findAllByProfileId(Long profileId);
    Optional<Order> findById(Long id);
    Order save(Order order);
    boolean existsByProductId(Long productId);
}
