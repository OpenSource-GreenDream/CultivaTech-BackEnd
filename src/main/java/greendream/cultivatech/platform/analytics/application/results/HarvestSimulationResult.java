package greendream.cultivatech.platform.analytics.application.results;

import greendream.cultivatech.platform.monitoring.domain.model.enums.SensorParameter;

import java.util.Date;
import java.util.List;
import java.util.Map;

public record HarvestSimulationResult(
        Long fieldId,
        Long productId,
        String cropType,
        String projectionPeriod,
        Date observationPeriodStart,
        Date observationPeriodEnd,
        Double cultivatedAreaHectares,
        Integer stockQuantity,
        Double baselineYieldTonsPerHectare,
        String baselineSource,
        Integer referenceYear,
        String geographicScope,
        String referenceSource,
        Double referenceProductionTons,
        Double projectedYieldTons,
        Double conditionAdjustmentFactor,
        String adjustmentMethodology,
        List<String> simulationAssumptions,
        List<String> quantitativeLimitations,
        List<SensorStatisticsResult> sensorStatistics,
        List<AgriculturalConditionResult> agriculturalConditions,
        DataQualityResult dataQuality,
        List<String> warnings,
        List<String> missingInformation,
        Date generatedAt
) {
    public record AgriculturalConditionResult(
            String parameter,
            String unit,
            String state,
            Double meanValue,
            Double referenceValue
    ) {
    }

    public record DataQualityResult(
            List<String> availableParameters,
            List<String> missingParameters,
            Map<String, Integer> sampleCounts,
            boolean baselineAvailable,
            List<String> missingRanges,
            int invalidMeasurements
    ) {
    }
}
