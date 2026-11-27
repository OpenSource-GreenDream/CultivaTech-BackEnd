package greendream.cultivatech.platform.analytics.interfaces.rest;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import greendream.cultivatech.platform.analytics.application.commandservices.AnalyticsCommandService;
import greendream.cultivatech.platform.analytics.application.commands.*;
import greendream.cultivatech.platform.analytics.application.querieservices.AnalyticsQueryService;
import greendream.cultivatech.platform.analytics.application.results.AnalyticsReportResult;
import greendream.cultivatech.platform.analytics.application.results.HarvestSimulationResult;
import greendream.cultivatech.platform.analytics.application.results.SensorStatisticsResult;
import greendream.cultivatech.platform.monitoring.domain.model.enums.SensorParameter;
import greendream.cultivatech.platform.analytics.domain.model.aggregates.HarvestProjection;
import greendream.cultivatech.platform.analytics.domain.model.aggregates.HarvestRecord;
import greendream.cultivatech.platform.shared.domain.exceptions.NotFoundException;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController @RequestMapping("/api/v1")
@Tag(name = "Analytics")
public class AnalyticsController {
    private final AnalyticsCommandService commands; private final AnalyticsQueryService queries;
    public AnalyticsController(AnalyticsCommandService commands,AnalyticsQueryService queries){this.commands=commands;this.queries=queries;}

    @PostMapping(value="/devices/{deviceId}/reports",consumes=MediaType.APPLICATION_JSON_VALUE,produces=MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> generateReports(@PathVariable Long deviceId,@Valid @RequestBody ReportRequest request){
        return commands.generateReports(new GenerateReportsCommand(deviceId,request.from(),request.to())).fold(
                values->ResponseEntity.status(HttpStatus.CREATED).body(values.stream().map(ReportResource::from).toList()),this::error);
    }
    @GetMapping("/devices/{deviceId}/reports") public ResponseEntity<?> reports(@PathVariable Long deviceId){
        return queries.reports(deviceId).fold(values->ResponseEntity.ok(values.stream().map(ReportResource::from).toList()),this::error);
    }
    @GetMapping(value="/devices/{deviceId}/statistics",produces=MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> statistics(@PathVariable Long deviceId,
            @RequestParam(required=false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date from,
            @RequestParam(required=false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date to){
        return queries.statistics(deviceId,from,to).fold(
                values->ResponseEntity.ok(values.stream().map(StatisticsResource::from).toList()),this::error);
    }
    @PostMapping(value="/harvest-records",consumes=MediaType.APPLICATION_JSON_VALUE,produces=MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> registerHarvest(@Valid @RequestBody HarvestRecordRequest r){
        return commands.registerHarvest(new RegisterHarvestRecordCommand(r.fieldId(),r.cropType(),r.season(),r.harvestedAreaM2(),r.harvestedQuantityTons(),r.harvestedAt()))
                .fold(value->ResponseEntity.status(HttpStatus.CREATED).body(HarvestRecordResource.from(value)),this::error);
    }
    @GetMapping("/fields/{fieldId}/harvest-records") public ResponseEntity<?> harvestRecords(@PathVariable Long fieldId,@RequestParam String cropType){
        return queries.harvestRecords(fieldId,cropType).fold(values->ResponseEntity.ok(values.stream().map(HarvestRecordResource::from).toList()),this::error);
    }
    @PostMapping(value="/fields/{fieldId}/harvest-projections",consumes=MediaType.APPLICATION_JSON_VALUE,produces=MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> generateProjection(@PathVariable Long fieldId,@Valid @RequestBody ProjectionRequest r){
        return commands.generateProjection(new GenerateHarvestProjectionCommand(fieldId,r.cropType(),r.projectionPeriod()))
                .fold(value->ResponseEntity.status(HttpStatus.CREATED).body(ProjectionResource.from(value)),this::error);
    }
    @PostMapping(value="/fields/{fieldId}/harvest-simulations",consumes=MediaType.APPLICATION_JSON_VALUE,produces=MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> simulateHarvest(@PathVariable Long fieldId,@Valid @RequestBody SimulationRequest r){
        return commands.simulateHarvest(new HarvestSimulationCommand(fieldId,r.productId(),r.cropType(),r.projectionPeriod(),r.observationFrom(),r.observationTo()))
                .fold(value->ResponseEntity.ok(SimulationResource.from(value)),this::error);
    }
    @GetMapping("/fields/{fieldId}/harvest-projections") public ResponseEntity<?> projections(@PathVariable Long fieldId){
        return queries.projections(fieldId).fold(values->ResponseEntity.ok(values.stream().map(ProjectionResource::from).toList()),this::error);
    }
    private ResponseEntity<Problem> error(String key){
        if("error.device.not-found".equals(key) || "error.field.not-found".equals(key) || "errors.not-found".equals(key)) throw new NotFoundException(key);
        return ResponseEntity.badRequest().body(new Problem(key));
    }
    public record Problem(String error) { }
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public record ReportRequest(@NotNull Date from,@NotNull Date to) { }
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public record HarvestRecordRequest(@NotNull Long fieldId,@NotBlank @Size(max=80) String cropType,@NotBlank @Size(max=20) String season,
            @Positive double harvestedAreaM2,@Positive double harvestedQuantityTons,@NotNull Date harvestedAt) { }
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public record ProjectionRequest(@NotBlank @Size(max=80) String cropType,@NotBlank @Size(max=20) String projectionPeriod) { }
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public record SimulationRequest(Long productId,@NotBlank @Size(max=80) String cropType,@NotBlank @Size(max=20) String projectionPeriod,
            @DateTimeFormat(iso=DateTimeFormat.ISO.DATE_TIME) Date observationFrom,
            @DateTimeFormat(iso=DateTimeFormat.ISO.DATE_TIME) Date observationTo) { }
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public record ReportResource(Long id,Long deviceId,SensorParameter parameter,String unit,Date periodStart,Date periodEnd,int sampleCount,
            Date generatedAt,double meanValue,double variance,double standardDeviation,String technicalInterpretation,Date createdAt,Date updatedAt){
        static ReportResource from(AnalyticsReportResult r){return new ReportResource(r.id(),r.deviceId(),r.parameter(),r.unit(),r.periodStart(),r.periodEnd(),r.sampleCount(),r.generatedAt(),r.meanValue(),r.variance(),r.standardDeviation(),r.technicalInterpretation(),r.createdAt(),r.updatedAt());}
    }
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public record StatisticsResource(Long deviceId,SensorParameter parameter,String unit,Date periodStart,Date periodEnd,int sampleCount,
            double meanValue,double variance,double standardDeviation,String technicalInterpretation){
        static StatisticsResource from(SensorStatisticsResult r){return new StatisticsResource(r.deviceId(),r.parameter(),r.unit(),r.periodStart(),r.periodEnd(),r.sampleCount(),r.meanValue(),r.variance(),r.standardDeviation(),r.technicalInterpretation());}
    }
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public record HarvestRecordResource(Long id,Long fieldId,String cropType,String season,double harvestedAreaM2,double harvestedQuantityTons,double yieldTonsPerHectare,Date harvestedAt,Date createdAt){
        static HarvestRecordResource from(HarvestRecord r){return new HarvestRecordResource(r.getId(),r.getFieldId(),r.getCropType(),r.getSeason(),r.getHarvestedAreaM2(),r.getHarvestedQuantityTons(),r.yieldTonsPerHectare(),r.getHarvestedAt(),r.getCreatedAt());}
    }
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public record ProjectionResource(Long id,Long fieldId,String cropType,double projectedYieldTons,double historicalYieldTons,String projectionPeriod,double confidenceScore,Date generatedAt,Date createdAt){
        static ProjectionResource from(HarvestProjection p){return new ProjectionResource(p.getId(),p.getFieldId(),p.getCropType(),p.getProjectedYieldTons(),p.getHistoricalYieldTons(),p.getProjectionPeriod(),p.getConfidenceScore(),p.getGeneratedAt(),p.getCreatedAt());}
    }
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public record SimulationResource(Long fieldId,Long productId,String cropType,String projectionPeriod,Date observationPeriodStart,Date observationPeriodEnd,
            Double cultivatedAreaHectares,Integer stockQuantity,Double baselineYieldTonsPerHectare,String baselineSource,Integer referenceYear,String geographicScope,
            String referenceSource,Double referenceProductionTons,Double projectedYieldTons,Double conditionAdjustmentFactor,String adjustmentMethodology,
            List<String> simulationAssumptions,List<String> quantitativeLimitations,List<StatisticsResource> sensorStatistics,
            List<ConditionResource> agriculturalConditions,DataQualityResource dataQuality,List<String> warnings,List<String> missingInformation,Date generatedAt){
        static SimulationResource from(HarvestSimulationResult r){return new SimulationResource(r.fieldId(),r.productId(),r.cropType(),r.projectionPeriod(),r.observationPeriodStart(),r.observationPeriodEnd(),
                r.cultivatedAreaHectares(),r.stockQuantity(),r.baselineYieldTonsPerHectare(),r.baselineSource(),r.referenceYear(),r.geographicScope(),r.referenceSource(),
                r.referenceProductionTons(),r.projectedYieldTons(),r.conditionAdjustmentFactor(),r.adjustmentMethodology(),r.simulationAssumptions(),r.quantitativeLimitations(),
                r.sensorStatistics().stream().map(StatisticsResource::from).toList(),
                r.agriculturalConditions().stream().map(ConditionResource::from).toList(),
                DataQualityResource.from(r.dataQuality()),r.warnings(),r.missingInformation(),r.generatedAt());}
    }
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public record ConditionResource(String parameter,String unit,String state,Double meanValue,Double referenceValue){
        static ConditionResource from(HarvestSimulationResult.AgriculturalConditionResult r){return new ConditionResource(r.parameter(),r.unit(),r.state(),r.meanValue(),r.referenceValue());}
    }
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public record DataQualityResource(List<String> availableParameters,List<String> missingParameters,Map<String,Integer> sampleCounts,boolean baselineAvailable,List<String> missingRanges,int invalidMeasurements){
        static DataQualityResource from(HarvestSimulationResult.DataQualityResult r){return new DataQualityResource(r.availableParameters(),r.missingParameters(),r.sampleCounts(),r.baselineAvailable(),r.missingRanges(),r.invalidMeasurements());}
    }
}
