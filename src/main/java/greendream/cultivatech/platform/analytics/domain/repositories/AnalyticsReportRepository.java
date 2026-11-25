package greendream.cultivatech.platform.analytics.domain.repositories;
import greendream.cultivatech.platform.analytics.domain.model.aggregates.AnalyticsReport;
import java.util.List;
public interface AnalyticsReportRepository { AnalyticsReport save(AnalyticsReport report); List<AnalyticsReport> findByDeviceId(Long deviceId); }
