package greendream.cultivatech.platform.analytics.application.internal.queryservices;

import greendream.cultivatech.platform.analytics.application.ports.DeviceTelemetry;
import greendream.cultivatech.platform.analytics.application.querieservices.AnalyticsQueryService;
import greendream.cultivatech.platform.analytics.application.results.AnalyticsReportResult;
import greendream.cultivatech.platform.analytics.application.results.SensorStatisticsResult;
import greendream.cultivatech.platform.analytics.domain.model.aggregates.AnalyticsReport;
import greendream.cultivatech.platform.analytics.domain.repositories.AnalyticsReportRepository;
import greendream.cultivatech.platform.analytics.domain.repositories.HarvestProjectionRepository;
import greendream.cultivatech.platform.analytics.domain.repositories.HarvestRecordRepository;
import greendream.cultivatech.platform.monitoring.domain.model.aggregates.SensorReading;
import greendream.cultivatech.platform.shared.application.result.Result;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AnalyticsQueryServiceImpl implements AnalyticsQueryService {
    private final AnalyticsReportRepository reports;
    private final HarvestRecordRepository harvests;
    private final HarvestProjectionRepository projections;
    private final DeviceTelemetry telemetry;

    public AnalyticsQueryServiceImpl(AnalyticsReportRepository reports,
                                     HarvestRecordRepository harvests,
                                     HarvestProjectionRepository projections,
                                     DeviceTelemetry telemetry) {
        this.reports = reports;
        this.harvests = harvests;
        this.projections = projections;
        this.telemetry = telemetry;
    }

    @Transactional(readOnly = true)
    public Result<List<AnalyticsReportResult>, String> reports(Long id) {
        if (id == null || id <= 0) {
            return Result.failure("error.analytics.device-id.invalid");
        }
        return Result.success(reports.findByDeviceId(id).stream().map(r -> new AnalyticsReportResult(
                r.getId(), r.getDeviceId(), r.getParameter(), r.getUnit(), r.getPeriodStart(), r.getPeriodEnd(),
                r.getSampleCount(), r.getGeneratedAt(), r.getMeanValue(), r.getVariance(),
                r.getStandardDeviation(), r.getTechnicalInterpretation(), r.getCreatedAt(), r.getUpdatedAt()
        )).toList());
    }

    @Transactional(readOnly = true)
    public Result<List<SensorStatisticsResult>, String> statistics(Long deviceId, Date from, Date to) {
        if (deviceId == null || deviceId <= 0) {
            return Result.failure("error.analytics.device-id.invalid");
        }
        if ((from == null) != (to == null)) {
            return Result.failure("error.analytics.period.invalid");
        }
        if (from != null && to != null && from.after(to)) {
            return Result.failure("error.analytics.period.invalid");
        }
        if (!telemetry.deviceExists(deviceId)) {
            return Result.failure("error.device.not-found");
        }

        List<SensorReading> readings = telemetry.readings(deviceId);
        List<SensorReading> filtered = readings;
        if (from != null && to != null) {
            filtered = readings.stream()
                    .filter(reading -> !reading.getRecordedAt().before(from) && !reading.getRecordedAt().after(to))
                    .toList();
        }

        Map<String, List<SensorReading>> groups = filtered.stream()
                .collect(Collectors.groupingBy(
                        reading -> reading.getParameter().name() + "\u0000" + reading.getUnit(),
                        LinkedHashMap::new,
                        Collectors.toList()
                ));

        List<SensorStatisticsResult> result = new ArrayList<>();
        groups.values().forEach(group -> {
            SensorReading first = group.get(0);
            Date periodStart;
            Date periodEnd;
            if (from != null && to != null) {
                periodStart = new Date(from.getTime());
                periodEnd = new Date(to.getTime());
            } else {
                periodStart = group.stream()
                        .map(SensorReading::getRecordedAt)
                        .min(Comparator.naturalOrder())
                        .orElseThrow();
                periodEnd = group.stream()
                        .map(SensorReading::getRecordedAt)
                        .max(Comparator.naturalOrder())
                        .orElseThrow();
            }

            AnalyticsReport report = AnalyticsReport.calculate(
                    deviceId,
                    first.getParameter(),
                    first.getUnit(),
                    periodStart,
                    periodEnd,
                    group.stream().map(SensorReading::getValue).toList()
            );

            result.add(new SensorStatisticsResult(
                    report.getDeviceId(),
                    report.getParameter(),
                    report.getUnit(),
                    report.getPeriodStart(),
                    report.getPeriodEnd(),
                    report.getSampleCount(),
                    report.getMeanValue(),
                    report.getVariance(),
                    report.getStandardDeviation(),
                    report.getTechnicalInterpretation()
            ));
        });

        return Result.success(List.copyOf(result));
    }

    @Transactional(readOnly = true)
    public Result<List<greendream.cultivatech.platform.analytics.domain.model.aggregates.HarvestRecord>, String> harvestRecords(Long field, String crop) {
        if (field == null || field <= 0) {
            return Result.failure("error.analytics.field-id.invalid");
        }
        if (crop == null || crop.isBlank()) {
            return Result.failure("error.analytics.crop-type.invalid");
        }
        return Result.success(harvests.findByFieldAndCrop(field, crop));
    }

    @Transactional(readOnly = true)
    public Result<List<greendream.cultivatech.platform.analytics.domain.model.aggregates.HarvestProjection>, String> projections(Long field) {
        if (field == null || field <= 0) {
            return Result.failure("error.analytics.field-id.invalid");
        }
        return Result.success(projections.findByFieldId(field));
    }
}
