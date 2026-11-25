package greendream.cultivatech.platform.analytics.application.results;
import java.util.Date;
public record AnalyticsReportResult(Long id,Long deviceId,String parameter,String unit,Date periodStart,Date periodEnd,int sampleCount,Date generatedAt,double meanValue,double variance,double standardDeviation,String technicalInterpretation,Date createdAt,Date updatedAt) { }
