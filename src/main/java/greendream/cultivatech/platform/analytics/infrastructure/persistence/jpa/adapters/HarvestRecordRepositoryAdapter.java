package greendream.cultivatech.platform.analytics.infrastructure.persistence.jpa.adapters;
import greendream.cultivatech.platform.analytics.domain.model.aggregates.HarvestRecord;
import greendream.cultivatech.platform.analytics.domain.repositories.HarvestRecordRepository;
import greendream.cultivatech.platform.analytics.infrastructure.persistence.jpa.entities.HarvestRecordEntity;
import greendream.cultivatech.platform.analytics.infrastructure.persistence.jpa.repositories.HarvestRecordJpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository public class HarvestRecordRepositoryAdapter implements HarvestRecordRepository {
 private final HarvestRecordJpaRepository jpa; public HarvestRecordRepositoryAdapter(HarvestRecordJpaRepository jpa){this.jpa=jpa;}
 public HarvestRecord save(HarvestRecord r){HarvestRecordEntity e=new HarvestRecordEntity();e.setFieldId(r.getFieldId());e.setCropType(r.getCropType());e.setSeason(r.getSeason());e.setHarvestedAreaM2(r.getHarvestedAreaM2());e.setHarvestedQuantityTons(r.getHarvestedQuantityTons());e.setHarvestedAt(r.getHarvestedAt());return domain(jpa.save(e));}
 public List<HarvestRecord> findByFieldAndCrop(Long field,String crop){return jpa.findAllByFieldIdAndCropTypeIgnoreCaseOrderByHarvestedAtDesc(field,crop).stream().map(this::domain).toList();}
 private HarvestRecord domain(HarvestRecordEntity e){return HarvestRecord.restore(e.getId(),e.getFieldId(),e.getCropType(),e.getSeason(),e.getHarvestedAreaM2(),e.getHarvestedQuantityTons(),e.getHarvestedAt(),e.getCreatedAt());}
}
