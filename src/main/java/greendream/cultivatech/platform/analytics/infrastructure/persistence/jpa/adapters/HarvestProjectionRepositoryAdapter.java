package greendream.cultivatech.platform.analytics.infrastructure.persistence.jpa.adapters;
import greendream.cultivatech.platform.analytics.domain.model.aggregates.HarvestProjection;
import greendream.cultivatech.platform.analytics.domain.repositories.HarvestProjectionRepository;
import greendream.cultivatech.platform.analytics.infrastructure.persistence.jpa.entities.HarvestProjectionEntity;
import greendream.cultivatech.platform.analytics.infrastructure.persistence.jpa.repositories.HarvestProjectionJpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository public class HarvestProjectionRepositoryAdapter implements HarvestProjectionRepository {
 private final HarvestProjectionJpaRepository jpa; public HarvestProjectionRepositoryAdapter(HarvestProjectionJpaRepository jpa){this.jpa=jpa;}
 public HarvestProjection save(HarvestProjection p){HarvestProjectionEntity e=new HarvestProjectionEntity();e.setFieldId(p.getFieldId());e.setCropType(p.getCropType());e.setProjectedYieldTons(p.getProjectedYieldTons());e.setHistoricalYieldTons(p.getHistoricalYieldTons());e.setProjectionPeriod(p.getProjectionPeriod());e.setConfidenceScore(p.getConfidenceScore());e.setGeneratedAt(p.getGeneratedAt());return domain(jpa.save(e));}
 public List<HarvestProjection> findByFieldId(Long id){return jpa.findAllByFieldIdOrderByGeneratedAtDesc(id).stream().map(this::domain).toList();}
 private HarvestProjection domain(HarvestProjectionEntity e){return HarvestProjection.restore(e.getId(),e.getFieldId(),e.getCropType(),e.getProjectedYieldTons(),e.getHistoricalYieldTons(),e.getProjectionPeriod(),e.getConfidenceScore(),e.getGeneratedAt(),e.getCreatedAt());}
}
