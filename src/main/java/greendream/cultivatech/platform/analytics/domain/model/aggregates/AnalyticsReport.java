package greendream.cultivatech.platform.analytics.domain.model.aggregates;

import greendream.cultivatech.platform.shared.domain.aggregates.AbstractDomainAggregateRoot;

import java.util.Date;
import java.util.List;

public class AnalyticsReport extends AbstractDomainAggregateRoot<AnalyticsReport> {
    private Long id;
    private final Long deviceId;
    private final String parameter;
    private final String unit;
    private final Date periodStart;
    private final Date periodEnd;
    private final int sampleCount;
    private final Date generatedAt;
    private final double meanValue;
    private final double variance;
    private final double standardDeviation;
    private final String technicalInterpretation;
    private Date createdAt;
    private Date updatedAt;

    private AnalyticsReport(Long deviceId, String parameter, String unit, Date periodStart, Date periodEnd,
                            int sampleCount, Date generatedAt, double meanValue, double variance,
                            double standardDeviation, String technicalInterpretation) {
        if (deviceId == null || deviceId <= 0) throw new IllegalArgumentException("error.analytics.device-id.invalid");
        if (parameter == null || parameter.isBlank() || unit == null || unit.isBlank()) throw new IllegalArgumentException("error.analytics.reading-metadata.invalid");
        if (periodStart == null || periodEnd == null || periodStart.after(periodEnd)) throw new IllegalArgumentException("error.analytics.period.invalid");
        this.deviceId = deviceId; this.parameter = parameter.trim(); this.unit = unit.trim();
        this.periodStart = new Date(periodStart.getTime()); this.periodEnd = new Date(periodEnd.getTime());
        this.sampleCount = sampleCount; this.generatedAt = generatedAt;
        this.meanValue = meanValue; this.variance = variance; this.standardDeviation = standardDeviation;
        this.technicalInterpretation = technicalInterpretation;
    }

    public static AnalyticsReport calculate(Long deviceId, String parameter, String unit, Date from, Date to, List<Double> values) {
        if (values == null || values.isEmpty()) throw new IllegalArgumentException("error.analytics.insufficient-readings");
        double mean = values.stream().mapToDouble(Double::doubleValue).average().orElseThrow();
        // Population variance: the report summarizes every reading in the requested interval.
        double variance = values.stream().mapToDouble(value -> Math.pow(value - mean, 2)).sum() / values.size();
        double deviation = Math.sqrt(variance);
        return new AnalyticsReport(deviceId, parameter, unit, from, to, values.size(), new Date(), mean, variance,
                deviation, "Calculated from " + values.size() + " sensor readings; this is a statistical summary, not an agronomic diagnosis.");
    }

    public static AnalyticsReport restore(Long id, Long deviceId, String parameter, String unit, Date from, Date to,
            int count, Date generated, double mean, double variance, double deviation, String interpretation, Date created, Date updated) {
        AnalyticsReport report = new AnalyticsReport(deviceId, parameter, unit, from, to, count, generated, mean, variance, deviation, interpretation);
        report.id = id; report.createdAt = created; report.updatedAt = updated; return report;
    }
    public Long getId() { return id; } public Long getDeviceId() { return deviceId; }
    public String getParameter() { return parameter; } public String getUnit() { return unit; }
    public Date getPeriodStart() { return new Date(periodStart.getTime()); } public Date getPeriodEnd() { return new Date(periodEnd.getTime()); }
    public int getSampleCount() { return sampleCount; } public Date getGeneratedAt() { return new Date(generatedAt.getTime()); }
    public double getMeanValue() { return meanValue; } public double getVariance() { return variance; }
    public double getStandardDeviation() { return standardDeviation; } public String getTechnicalInterpretation() { return technicalInterpretation; }
    public Date getCreatedAt() { return createdAt; } public Date getUpdatedAt() { return updatedAt; }
}
