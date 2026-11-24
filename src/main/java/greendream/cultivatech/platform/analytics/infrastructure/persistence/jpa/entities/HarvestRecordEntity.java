package greendream.cultivatech.platform.analytics.infrastructure.persistence.jpa.entities;

import greendream.cultivatech.platform.shared.infrastructure.persistence.jpa.AuditableAbstractPersistenceEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Date;

@Getter @Setter @NoArgsConstructor @Entity @Table(name="harvest_records")
public class HarvestRecordEntity extends AuditableAbstractPersistenceEntity {
    @Column(name="field_id",nullable=false) private Long fieldId;
    @Column(name="crop_type",nullable=false,length=80) private String cropType;
    @Column(name="season",nullable=false,length=20) private String season;
    @Column(name="harvested_area_m2",nullable=false) private Double harvestedAreaM2;
    @Column(name="harvested_quantity_tons",nullable=false) private Double harvestedQuantityTons;
    @Temporal(TemporalType.TIMESTAMP) @Column(name="harvested_at",nullable=false) private Date harvestedAt;
}
