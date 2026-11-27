package greendream.cultivatech.platform.analytics.application.commandservices;
import greendream.cultivatech.platform.analytics.application.commands.*;
import greendream.cultivatech.platform.analytics.application.results.AnalyticsReportResult;
import greendream.cultivatech.platform.analytics.application.results.HarvestSimulationResult;
import greendream.cultivatech.platform.analytics.domain.model.aggregates.HarvestProjection;
import greendream.cultivatech.platform.analytics.domain.model.aggregates.HarvestRecord;
import greendream.cultivatech.platform.shared.application.result.Result;
import java.util.List;
public interface AnalyticsCommandService {
 Result<List<AnalyticsReportResult>,String> generateReports(GenerateReportsCommand command);
 Result<HarvestRecord,String> registerHarvest(RegisterHarvestRecordCommand command);
 Result<HarvestProjection,String> generateProjection(GenerateHarvestProjectionCommand command);
 Result<HarvestSimulationResult,String> simulateHarvest(HarvestSimulationCommand command);
}
