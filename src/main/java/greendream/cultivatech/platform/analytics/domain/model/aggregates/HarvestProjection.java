package greendream.cultivatech.platform.analytics.domain.model.aggregates;

import greendream.cultivatech.platform.shared.domain.aggregates.AbstractDomainAggregateRoot;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class HarvestProjection extends AbstractDomainAggregateRoot<HarvestProjection> {
    private Long id; private final Long fieldId; private final String cropType; private final double projectedYieldTons;
    private final double historicalYieldTons; private final String projectionPeriod; private final double confidenceScore;
    private final Date generatedAt; private Date createdAt;

    private HarvestProjection(Long fieldId,String cropType,double projected,double historical,String period,double confidence,Date generatedAt) {
        this.fieldId=fieldId; this.cropType=cropType; this.projectedYieldTons=projected; this.historicalYieldTons=historical;
        this.projectionPeriod=period; this.confidenceScore=confidence; this.generatedAt=generatedAt;
    }
    /**
     * Uses the latest three actual harvests for the same field and crop. The expected total
     * is the mean yield per hectare multiplied by the most recent harvested area. The score
     * combines sample coverage (n/3) and stability (1 - min(coefficient of variation, 1));
     * it is a heuristic, not a probability or agronomic confidence interval.
     */
    public static HarvestProjection forecast(Long fieldId,String cropType,String period,List<HarvestRecord> history) {
        if(fieldId==null||fieldId<=0) throw new IllegalArgumentException("error.analytics.field-id.invalid");
        if(cropType==null||cropType.isBlank()||cropType.length()>80) throw new IllegalArgumentException("error.analytics.crop-type.invalid");
        if(period==null||period.isBlank()||period.length()>20) throw new IllegalArgumentException("error.analytics.projection-period.invalid");
        if(history==null) throw new IllegalArgumentException("error.analytics.harvest-history.required");
        List<HarvestRecord> records=history.stream().filter(r->r.getCropType().equalsIgnoreCase(cropType))
                .sorted(Comparator.comparing(HarvestRecord::getHarvestedAt).reversed()).limit(3).toList();
        if(records.size()<2) throw new IllegalArgumentException("error.analytics.insufficient-harvest-history");
        double meanRate=records.stream().mapToDouble(HarvestRecord::yieldTonsPerHectare).average().orElseThrow();
        double historical=records.stream().mapToDouble(HarvestRecord::getHarvestedQuantityTons).average().orElseThrow();
        double variance=records.stream().mapToDouble(r->Math.pow(r.yieldTonsPerHectare()-meanRate,2)).sum()/records.size();
        double coefficientOfVariation=meanRate==0?1:Math.sqrt(variance)/meanRate;
        double confidence=Math.min(records.size()/3d,1d)*(1d-Math.min(coefficientOfVariation,1d));
        double latestAreaHectares=records.getFirst().getHarvestedAreaM2()/10_000d;
        return new HarvestProjection(fieldId,cropType.trim(),meanRate*latestAreaHectares,historical,period.trim(),confidence,new Date());
    }
    public static HarvestProjection restore(Long id,Long fieldId,String crop,double projected,double historical,String period,double confidence,Date generated,Date created){
        HarvestProjection p=new HarvestProjection(fieldId,crop,projected,historical,period,confidence,generated);p.id=id;p.createdAt=created;return p;
    }
    public Long getId(){return id;} public Long getFieldId(){return fieldId;} public String getCropType(){return cropType;}
    public double getProjectedYieldTons(){return projectedYieldTons;} public double getHistoricalYieldTons(){return historicalYieldTons;}
    public String getProjectionPeriod(){return projectionPeriod;} public double getConfidenceScore(){return confidenceScore;}
    public Date getGeneratedAt(){return generatedAt;} public Date getCreatedAt(){return createdAt;}
}
