package greendream.cultivatech.platform.analytics.application.internal.queryservices;
import greendream.cultivatech.platform.analytics.application.querieservices.AnalyticsQueryService;
import greendream.cultivatech.platform.analytics.application.results.AnalyticsReportResult;
import greendream.cultivatech.platform.analytics.domain.repositories.*;
import greendream.cultivatech.platform.shared.application.result.Result;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
@Service public class AnalyticsQueryServiceImpl implements AnalyticsQueryService {
 private final AnalyticsReportRepository reports; private final HarvestRecordRepository harvests; private final HarvestProjectionRepository projections;
 public AnalyticsQueryServiceImpl(AnalyticsReportRepository reports,HarvestRecordRepository harvests,HarvestProjectionRepository projections){this.reports=reports;this.harvests=harvests;this.projections=projections;}
 @Transactional(readOnly=true) public Result<List<AnalyticsReportResult>,String> reports(Long id){if(id==null||id<=0)return Result.failure("error.analytics.device-id.invalid");return Result.success(reports.findByDeviceId(id).stream().map(r->new AnalyticsReportResult(r.getId(),r.getDeviceId(),r.getParameter(),r.getUnit(),r.getPeriodStart(),r.getPeriodEnd(),r.getSampleCount(),r.getGeneratedAt(),r.getMeanValue(),r.getVariance(),r.getStandardDeviation(),r.getTechnicalInterpretation(),r.getCreatedAt(),r.getUpdatedAt())).toList());}
 @Transactional(readOnly=true) public Result<List<greendream.cultivatech.platform.analytics.domain.model.aggregates.HarvestRecord>,String> harvestRecords(Long field,String crop){if(field==null||field<=0)return Result.failure("error.analytics.field-id.invalid");if(crop==null||crop.isBlank())return Result.failure("error.analytics.crop-type.invalid");return Result.success(harvests.findByFieldAndCrop(field,crop));}
 @Transactional(readOnly=true) public Result<List<greendream.cultivatech.platform.analytics.domain.model.aggregates.HarvestProjection>,String> projections(Long field){if(field==null||field<=0)return Result.failure("error.analytics.field-id.invalid");return Result.success(projections.findByFieldId(field));}
}
