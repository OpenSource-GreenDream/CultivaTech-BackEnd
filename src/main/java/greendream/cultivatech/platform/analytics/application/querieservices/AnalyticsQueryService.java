package greendream.cultivatech.platform.analytics.application.querieservices;
import greendream.cultivatech.platform.analytics.domain.model.aggregates.HarvestProjection;
import greendream.cultivatech.platform.analytics.domain.model.aggregates.HarvestRecord;
import greendream.cultivatech.platform.analytics.application.results.AnalyticsReportResult;
import greendream.cultivatech.platform.analytics.application.results.SensorStatisticsResult;
import greendream.cultivatech.platform.shared.application.result.Result;
import java.util.Date;
import java.util.List;
public interface AnalyticsQueryService {
 Result<List<AnalyticsReportResult>,String> reports(Long deviceId);
 Result<List<SensorStatisticsResult>,String> statistics(Long deviceId, Date from, Date to);
 Result<List<HarvestRecord>,String> harvestRecords(Long fieldId,String cropType);
 Result<List<HarvestProjection>,String> projections(Long fieldId);
}
