package greendream.cultivatech.platform.analytics.infrastructure.persistence.jpa.adapters;
import greendream.cultivatech.platform.analytics.domain.model.aggregates.AnalyticsReport;
import greendream.cultivatech.platform.analytics.domain.repositories.AnalyticsReportRepository;
import greendream.cultivatech.platform.monitoring.domain.model.enums.SensorParameter;
import greendream.cultivatech.platform.analytics.infrastructure.persistence.jpa.entities.AnalyticsReportEntity;
import greendream.cultivatech.platform.analytics.infrastructure.persistence.jpa.repositories.AnalyticsReportJpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository public class AnalyticsReportRepositoryAdapter implements AnalyticsReportRepository {
 private final AnalyticsReportJpaRepository jpa; public AnalyticsReportRepositoryAdapter(AnalyticsReportJpaRepository jpa){this.jpa=jpa;}
 public AnalyticsReport save(AnalyticsReport r){AnalyticsReportEntity e=new AnalyticsReportEntity();e.setDeviceId(r.getDeviceId());e.setParameter(r.getParameter().name());e.setUnit(r.getUnit());e.setPeriodStart(r.getPeriodStart());e.setPeriodEnd(r.getPeriodEnd());e.setSampleCount(r.getSampleCount());e.setGeneratedAt(r.getGeneratedAt());e.setMeanValue(r.getMeanValue());e.setVariance(r.getVariance());e.setStandardDeviation(r.getStandardDeviation());e.setTechnicalInterpretation(r.getTechnicalInterpretation());return domain(jpa.save(e));}
 public List<AnalyticsReport> findByDeviceId(Long id){return jpa.findAllByDeviceIdOrderByGeneratedAtDesc(id).stream().map(this::domain).toList();}
 private AnalyticsReport domain(AnalyticsReportEntity e){return AnalyticsReport.restore(e.getId(),e.getDeviceId(),SensorParameter.from(e.getParameter()),e.getUnit(),e.getPeriodStart(),e.getPeriodEnd(),e.getSampleCount(),e.getGeneratedAt(),e.getMeanValue(),e.getVariance(),e.getStandardDeviation(),e.getTechnicalInterpretation(),e.getCreatedAt(),e.getUpdatedAt());}
}
