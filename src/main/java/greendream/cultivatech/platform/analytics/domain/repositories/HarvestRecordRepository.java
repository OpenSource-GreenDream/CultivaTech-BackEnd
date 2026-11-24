package greendream.cultivatech.platform.analytics.domain.repositories;
import greendream.cultivatech.platform.analytics.domain.model.aggregates.HarvestRecord;
import java.util.List;
public interface HarvestRecordRepository { HarvestRecord save(HarvestRecord record); List<HarvestRecord> findByFieldAndCrop(Long fieldId,String cropType); }
