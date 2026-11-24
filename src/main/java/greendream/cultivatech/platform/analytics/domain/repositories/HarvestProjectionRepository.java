package greendream.cultivatech.platform.analytics.domain.repositories;
import greendream.cultivatech.platform.analytics.domain.model.aggregates.HarvestProjection;
import java.util.List;
public interface HarvestProjectionRepository { HarvestProjection save(HarvestProjection projection); List<HarvestProjection> findByFieldId(Long fieldId); }
