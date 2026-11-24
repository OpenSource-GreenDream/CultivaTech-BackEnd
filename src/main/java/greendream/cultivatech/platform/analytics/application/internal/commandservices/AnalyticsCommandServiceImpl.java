package greendream.cultivatech.platform.analytics.application.internal.commandservices;

import greendream.cultivatech.platform.analytics.application.commandservices.AnalyticsCommandService;
import greendream.cultivatech.platform.analytics.application.commands.*;
import greendream.cultivatech.platform.analytics.application.ports.DeviceTelemetry;
import greendream.cultivatech.platform.analytics.application.results.AnalyticsReportResult;
import greendream.cultivatech.platform.analytics.domain.model.aggregates.*;
import greendream.cultivatech.platform.analytics.domain.repositories.*;
import greendream.cultivatech.platform.shared.application.result.Result;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AnalyticsCommandServiceImpl implements AnalyticsCommandService {
    private final DeviceTelemetry telemetry;
    private final AnalyticsReportRepository reports;
    private final HarvestRecordRepository harvestRecords;
    private final HarvestProjectionRepository projections;
    public AnalyticsCommandServiceImpl(DeviceTelemetry telemetry,AnalyticsReportRepository reports,HarvestRecordRepository harvestRecords,HarvestProjectionRepository projections){
        this.telemetry=telemetry;this.reports=reports;this.harvestRecords=harvestRecords;this.projections=projections;
    }
    @Override @Transactional public Result<List<AnalyticsReportResult>,String> generateReports(GenerateReportsCommand c){
        if(c==null)return Result.failure("error.command.required");
        if(c.deviceId()==null||c.deviceId()<=0)return Result.failure("error.analytics.device-id.invalid");
        if(c.from()==null||c.to()==null||c.from().after(c.to()))return Result.failure("error.analytics.period.invalid");
        if(!telemetry.deviceExists(c.deviceId()))return Result.failure("error.device.not-found");
        Map<String,List<greendream.cultivatech.platform.monitoring.domain.model.aggregates.SensorReading>> groups=telemetry.readings(c.deviceId()).stream()
                .filter(r->!r.getRecordedAt().before(c.from())&&!r.getRecordedAt().after(c.to()))
                .collect(Collectors.groupingBy(r->r.getParameter()+"\u0000"+r.getUnit(),LinkedHashMap::new,Collectors.toList()));
        if(groups.isEmpty())return Result.failure("error.analytics.insufficient-readings");
        List<AnalyticsReportResult> result=new ArrayList<>();
        groups.values().forEach(group->{var first=group.getFirst();AnalyticsReport report=AnalyticsReport.calculate(c.deviceId(),first.getParameter(),first.getUnit(),c.from(),c.to(),group.stream().map(g->g.getValue()).toList());result.add(toResult(reports.save(report)));});
        return Result.success(List.copyOf(result));
    }
    @Override @Transactional public Result<HarvestRecord,String> registerHarvest(RegisterHarvestRecordCommand c){
        if(c==null)return Result.failure("error.command.required");
        try{return Result.success(harvestRecords.save(new HarvestRecord(c.fieldId(),c.cropType(),c.season(),c.harvestedAreaM2(),c.harvestedQuantityTons(),c.harvestedAt())));}
        catch(IllegalArgumentException e){return Result.failure(e.getMessage());}
    }
    @Override @Transactional public Result<HarvestProjection,String> generateProjection(GenerateHarvestProjectionCommand c){
        if(c==null)return Result.failure("error.command.required");
        try{return Result.success(projections.save(HarvestProjection.forecast(c.fieldId(),c.cropType(),c.projectionPeriod(),harvestRecords.findByFieldAndCrop(c.fieldId(),c.cropType()))));}
        catch(IllegalArgumentException e){return Result.failure(e.getMessage());}
    }
    private AnalyticsReportResult toResult(AnalyticsReport r){return new AnalyticsReportResult(r.getId(),r.getDeviceId(),r.getParameter(),r.getUnit(),r.getPeriodStart(),r.getPeriodEnd(),r.getSampleCount(),r.getGeneratedAt(),r.getMeanValue(),r.getVariance(),r.getStandardDeviation(),r.getTechnicalInterpretation(),r.getCreatedAt(),r.getUpdatedAt());}
}
