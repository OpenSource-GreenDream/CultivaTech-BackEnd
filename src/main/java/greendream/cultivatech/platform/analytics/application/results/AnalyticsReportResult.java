package greendream.cultivatech.platform.analytics.application.results;

import greendream.cultivatech.platform.monitoring.domain.model.enums.SensorParameter;

import java.util.Date;

public record AnalyticsReportResult(Long id, Long deviceId, SensorParameter parameter, String unit, Date periodStart, Date periodEnd, int sampleCount, Date generatedAt, double meanValue, double variance, double standardDeviation, String technicalInterpretation, Date createdAt, Date updatedAt) {
}
