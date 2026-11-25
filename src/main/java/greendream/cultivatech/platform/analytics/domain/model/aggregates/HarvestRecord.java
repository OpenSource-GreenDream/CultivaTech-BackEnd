package greendream.cultivatech.platform.analytics.domain.model.aggregates;

import greendream.cultivatech.platform.shared.domain.aggregates.AbstractDomainAggregateRoot;
import java.util.Date;

public class HarvestRecord extends AbstractDomainAggregateRoot<HarvestRecord> {
    private Long id;
    private final Long fieldId;
    private final String cropType;
    private final String season;
    private final double harvestedAreaM2;
    private final double harvestedQuantityTons;
    private final Date harvestedAt;
    private Date createdAt;

    public HarvestRecord(Long fieldId, String cropType, String season, double areaM2, double quantityTons, Date harvestedAt) {
        if (fieldId == null || fieldId <= 0) throw new IllegalArgumentException("error.analytics.field-id.invalid");
        if (cropType == null || cropType.isBlank() || cropType.length() > 80) throw new IllegalArgumentException("error.analytics.crop-type.invalid");
        if (season == null || season.isBlank() || season.length() > 20) throw new IllegalArgumentException("error.analytics.season.invalid");
        if (!Double.isFinite(areaM2) || areaM2 <= 0 || !Double.isFinite(quantityTons) || quantityTons <= 0) throw new IllegalArgumentException("error.analytics.harvest-quantity.invalid");
        if (harvestedAt == null) throw new IllegalArgumentException("error.analytics.harvest-date.required");
        this.fieldId=fieldId; this.cropType=cropType.trim(); this.season=season.trim(); this.harvestedAreaM2=areaM2;
        this.harvestedQuantityTons=quantityTons; this.harvestedAt=new Date(harvestedAt.getTime());
    }
    public static HarvestRecord restore(Long id, Long fieldId, String cropType, String season, double area, double quantity, Date harvestedAt, Date createdAt) {
        HarvestRecord record=new HarvestRecord(fieldId,cropType,season,area,quantity,harvestedAt); record.id=id; record.createdAt=createdAt; return record;
    }
    public double yieldTonsPerHectare() { return harvestedQuantityTons / (harvestedAreaM2 / 10_000d); }
    public Long getId(){return id;} public Long getFieldId(){return fieldId;} public String getCropType(){return cropType;}
    public String getSeason(){return season;} public double getHarvestedAreaM2(){return harvestedAreaM2;}
    public double getHarvestedQuantityTons(){return harvestedQuantityTons;} public Date getHarvestedAt(){return new Date(harvestedAt.getTime());} public Date getCreatedAt(){return createdAt;}
}
