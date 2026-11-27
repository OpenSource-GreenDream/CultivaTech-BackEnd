package greendream.cultivatech.platform.analytics.application.internal.commandservices;

import greendream.cultivatech.platform.analytics.application.commandservices.AnalyticsCommandService;
import greendream.cultivatech.platform.analytics.application.commands.*;
import greendream.cultivatech.platform.analytics.application.ports.DeviceTelemetry;
import greendream.cultivatech.platform.analytics.application.results.AnalyticsReportResult;
import greendream.cultivatech.platform.analytics.application.results.HarvestSimulationResult;
import greendream.cultivatech.platform.analytics.application.results.SensorStatisticsResult;
import greendream.cultivatech.platform.analytics.domain.model.CropYieldReference;
import greendream.cultivatech.platform.analytics.domain.model.aggregates.*;
import greendream.cultivatech.platform.analytics.domain.repositories.*;
import greendream.cultivatech.platform.commercial.domain.repositories.ProductRepository;
import greendream.cultivatech.platform.inventory.domain.repositories.InventoryRepository;
import greendream.cultivatech.platform.monitoring.domain.model.enums.SensorParameter;
import greendream.cultivatech.platform.monitoring.domain.repositories.DeviceRepository;
import greendream.cultivatech.platform.profile.domain.repositories.FieldRepository;
import greendream.cultivatech.platform.profile.domain.repositories.ProfileRepository;
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
    private final FieldRepository fields;
    private final ProfileRepository profiles;
    private final InventoryRepository inventories;
    private final ProductRepository products;
    private final DeviceRepository devices;
    public AnalyticsCommandServiceImpl(DeviceTelemetry telemetry,AnalyticsReportRepository reports,HarvestRecordRepository harvestRecords,HarvestProjectionRepository projections,
                                     FieldRepository fields,ProfileRepository profiles,InventoryRepository inventories,ProductRepository products,DeviceRepository devices){
        this.telemetry=telemetry;this.reports=reports;this.harvestRecords=harvestRecords;this.projections=projections;
        this.fields=fields;this.profiles=profiles;this.inventories=inventories;this.products=products;this.devices=devices;
    }
    @Override @Transactional public Result<List<AnalyticsReportResult>,String> generateReports(GenerateReportsCommand c){
        if(c==null)return Result.failure("error.command.required");
        if(c.deviceId()==null||c.deviceId()<=0)return Result.failure("error.analytics.device-id.invalid");
        if(c.from()==null||c.to()==null||c.from().after(c.to()))return Result.failure("error.analytics.period.invalid");
        if(!telemetry.deviceExists(c.deviceId()))return Result.failure("error.device.not-found");
        Map<String,List<greendream.cultivatech.platform.monitoring.domain.model.aggregates.SensorReading>> groups=telemetry.readings(c.deviceId()).stream()
                .filter(r->!r.getRecordedAt().before(c.from())&&!r.getRecordedAt().after(c.to()))
                .collect(Collectors.groupingBy(r->r.getParameter().name()+"\u0000"+r.getUnit(),LinkedHashMap::new,Collectors.toList()));
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
    @Override
    @Transactional
    public Result<HarvestSimulationResult, String> simulateHarvest(HarvestSimulationCommand command) {
        if (command == null) return Result.failure("error.command.required");
        if (command.fieldId() == null || command.fieldId() <= 0) return Result.failure("error.analytics.field-id.invalid");
        if (command.cropType() == null || command.cropType().isBlank()) return Result.failure("error.analytics.crop-type.invalid");
        if (command.projectionPeriod() == null || command.projectionPeriod().isBlank()) return Result.failure("error.analytics.projection-period.invalid");
        if ((command.observationFrom() == null) != (command.observationTo() == null)) return Result.failure("error.analytics.period.invalid");
        if (command.observationFrom() != null && command.observationFrom().after(command.observationTo())) return Result.failure("error.analytics.period.invalid");

        var field = fields.findById(new greendream.cultivatech.platform.profile.domain.model.valueobjects.FieldId(command.fieldId())).orElse(null);
        if (field == null) return Result.failure("error.field.not-found");

        var profile = profiles.findById(field.profileId()).orElse(null);
        if (profile == null) return Result.failure("error.profile.not-found");

        // Product/inventory validation
        Integer stockQuantity = null;
        if (command.productId() != null) {
            var product = products.findById(command.productId()).orElse(null);
            if (product == null) return Result.failure("errors.not-found");
            if (product.fieldId() != null && !product.fieldId().equals(command.fieldId())) return Result.failure("error.product.field.inconsistent");
            var inventory = inventories.findById(new greendream.cultivatech.platform.inventory.domain.model.valueobjects.InventoryId(product.inventoryId())).orElse(null);
            if (inventory == null) return Result.failure("errors.not-found");
            if (!inventory.fieldId().value().equals(command.fieldId())) return Result.failure("error.product.field.inconsistent");
            stockQuantity = inventory.stockQuantity().value();
        }

        // Devices for field
        var fieldDevices = devices.findByFieldId(new greendream.cultivatech.platform.monitoring.domain.model.valueobjects.FieldId(command.fieldId()));

        // Readings
        Date periodStart = command.observationFrom();
        Date periodEnd = command.observationTo();
        List<greendream.cultivatech.platform.monitoring.domain.model.aggregates.SensorReading> allReadings = new ArrayList<>();
        for (var device : fieldDevices) {
            var deviceId = device.id().orElse(null);
            if (deviceId == null) continue;
            var readings = telemetry.readings(deviceId.value());
            if (command.observationFrom() != null && command.observationTo() != null) {
                readings = readings.stream()
                        .filter(r -> !r.getRecordedAt().before(command.observationFrom()) && !r.getRecordedAt().after(command.observationTo()))
                        .toList();
            }
            allReadings.addAll(readings);
        }
        if (periodStart == null && !allReadings.isEmpty()) {
            periodStart = allReadings.stream().map(greendream.cultivatech.platform.monitoring.domain.model.aggregates.SensorReading::getRecordedAt).min(java.util.Comparator.naturalOrder()).orElse(null);
            periodEnd = allReadings.stream().map(greendream.cultivatech.platform.monitoring.domain.model.aggregates.SensorReading::getRecordedAt).max(java.util.Comparator.naturalOrder()).orElse(null);
        }

        // Group by parameter+unit
        Map<String, List<greendream.cultivatech.platform.monitoring.domain.model.aggregates.SensorReading>> groups = allReadings.stream()
                .collect(Collectors.groupingBy(r -> r.getParameter().name() + "\\u0000" + r.getUnit(), LinkedHashMap::new, Collectors.toList()));

        List<SensorStatisticsResult> stats = new ArrayList<>();
        List<HarvestSimulationResult.AgriculturalConditionResult> conditions = new ArrayList<>();
        Map<String, Integer> sampleCounts = new LinkedHashMap<>();
        List<String> availableParams = new ArrayList<>();
        List<String> missingRanges = new ArrayList<>();

        for (var group : groups.values()) {
            if (group.isEmpty()) continue;
            var first = group.get(0);
            Date ps = periodStart != null ? new Date(periodStart.getTime()) : group.stream().map(greendream.cultivatech.platform.monitoring.domain.model.aggregates.SensorReading::getRecordedAt).min(java.util.Comparator.naturalOrder()).orElseThrow();
            Date pe = periodEnd != null ? new Date(periodEnd.getTime()) : group.stream().map(greendream.cultivatech.platform.monitoring.domain.model.aggregates.SensorReading::getRecordedAt).max(java.util.Comparator.naturalOrder()).orElseThrow();
            var report = AnalyticsReport.calculate(field.id().get().value(), first.getParameter(), first.getUnit(), ps, pe, group.stream().map(greendream.cultivatech.platform.monitoring.domain.model.aggregates.SensorReading::getValue).toList());
            stats.add(new SensorStatisticsResult(null, report.getParameter(), report.getUnit(), report.getPeriodStart(), report.getPeriodEnd(), report.getSampleCount(), report.getMeanValue(), report.getVariance(), report.getStandardDeviation(), report.getTechnicalInterpretation()));
            sampleCounts.put(first.getParameter().name(), group.size());
            availableParams.add(first.getParameter().name());

            String state;
            Double ref = null;
            if (first.getParameter() == SensorParameter.MOISTURE) {
                ref = profile.moistureThreshold().value();
                state = report.getMeanValue() >= ref ? "WITHIN_RANGE" : "OUTSIDE_RANGE";
            } else {
                state = "NO_REFERENCE_RANGE";
                missingRanges.add(first.getParameter().name());
            }
            conditions.add(new HarvestSimulationResult.AgriculturalConditionResult(first.getParameter().name(), first.getUnit(), state, report.getMeanValue(), ref));
        }

        List<String> allParams = java.util.Arrays.stream(SensorParameter.values()).map(Enum::name).toList();
        List<String> missingParams = new ArrayList<>(allParams);
        missingParams.removeAll(availableParams);

        // Baseline
        Double baselineYield = null;
        String baselineSource = "NONE";
        Integer referenceYear = null;
        String geographicScope = null;
        String referenceSource = null;
        var history = harvestRecords.findByFieldAndCrop(command.fieldId(), command.cropType());
        if (history.size() >= 2) {
            var records = history.stream()
                    .filter(r -> r.getCropType().equalsIgnoreCase(command.cropType()))
                    .sorted(java.util.Comparator.comparing(greendream.cultivatech.platform.analytics.domain.model.aggregates.HarvestRecord::getHarvestedAt).reversed())
                    .limit(3)
                    .toList();
            if (records.size() >= 2) {
                baselineYield = records.stream().mapToDouble(greendream.cultivatech.platform.analytics.domain.model.aggregates.HarvestRecord::yieldTonsPerHectare).average().orElse(0.0);
                baselineSource = "HISTORICAL_HARVESTS";
                referenceSource = "HarvestRecord";
            }
        }
        if (baselineYield == null) {
            var cropRef = CropYieldReference.find(command.cropType());
            if (cropRef.isPresent()) {
                var ref = cropRef.get();
                baselineYield = ref.referenceYieldTonsPerHectare();
                baselineSource = "DOCUMENTED_CROP_REFERENCE";
                referenceYear = ref.referenceYear();
                geographicScope = ref.geographicScope();
                referenceSource = ref.source();
            }
        }

        double areaHa = field.size().value() / 10000.0;
        Double referenceProduction = baselineYield == null ? null : baselineYield * areaHa;

        // Condition adjustment factor (heuristic based on moisture)
        Double conditionFactor = null;
        String adjustmentMethodology = null;
        var moistureStat = stats.stream()
                .filter(s -> s.parameter() == greendream.cultivatech.platform.monitoring.domain.model.enums.SensorParameter.MOISTURE && "%".equals(s.unit()))
                .findFirst()
                .orElse(null);
        if (moistureStat != null && profile.moistureThreshold().value() > 0.0) {
            conditionFactor = Math.min(1.0, moistureStat.meanValue() / profile.moistureThreshold().value());
            adjustmentMethodology = "Heuristic: min(1, meanMoisture / profileMoistureThreshold)";
        }

        Double projectedYield = null;
        if (referenceProduction != null && conditionFactor != null) {
            projectedYield = referenceProduction * conditionFactor;
        }

        List<String> simulationAssumptions = new ArrayList<>();
        simulationAssumptions.add("The entire field area is assumed to be cultivated.");
        simulationAssumptions.add("Moisture adjustment is an academic heuristic, not an agronomically validated model.");

        List<String> quantitativeLimitations = new ArrayList<>();
        if (conditionFactor == null) {
            quantitativeLimitations.add("Condition adjustment factor is unavailable; projected yield cannot be calculated.");
        }
        if (baselineYield == null) {
            quantitativeLimitations.add("No quantitative baseline is available.");
        }

        List<String> warnings = new ArrayList<>();
        List<String> missingInfo = new ArrayList<>();
        if (availableParams.isEmpty()) {
            warnings.add("No sensor readings available for the selected field and period.");
        }
        if (baselineYield == null) {
            missingInfo.add("No historical harvest baseline available; projected yield cannot be calculated.");
        }
        if (command.productId() == null) {
            missingInfo.add("productId not provided; stock quantity is not associated.");
        }
        if (!missingRanges.isEmpty()) {
            missingInfo.add("No agronomic reference range available for: " + String.join(", ", missingRanges));
        }

        var dataQuality = new HarvestSimulationResult.DataQualityResult(
                availableParams,
                missingParams,
                sampleCounts,
                baselineYield != null,
                missingRanges,
                0
        );

        var result = new HarvestSimulationResult(
                command.fieldId(),
                command.productId(),
                command.cropType(),
                command.projectionPeriod(),
                periodStart,
                periodEnd,
                areaHa,
                stockQuantity,
                baselineYield,
                baselineSource,
                referenceYear,
                geographicScope,
                referenceSource,
                referenceProduction,
                projectedYield,
                conditionFactor,
                adjustmentMethodology,
                simulationAssumptions,
                quantitativeLimitations,
                stats,
                conditions,
                dataQuality,
                warnings,
                missingInfo,
                new Date()
        );
        return Result.success(result);
    }

    private AnalyticsReportResult toResult(AnalyticsReport r){return new AnalyticsReportResult(r.getId(),r.getDeviceId(),r.getParameter(),r.getUnit(),r.getPeriodStart(),r.getPeriodEnd(),r.getSampleCount(),r.getGeneratedAt(),r.getMeanValue(),r.getVariance(),r.getStandardDeviation(),r.getTechnicalInterpretation(),r.getCreatedAt(),r.getUpdatedAt());}
}
