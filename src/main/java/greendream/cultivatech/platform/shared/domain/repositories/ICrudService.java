package greendream.cultivatech.platform.shared.domain.repositories;

import java.util.List;
import java.util.Optional;

public interface ICrudService<T, ID> {
    List<T> findAll();
    T save(T t);
    T update(T t);
    Optional<T> findById(ID id);
    void deleteById(ID id);
    void deleteAll();
}
