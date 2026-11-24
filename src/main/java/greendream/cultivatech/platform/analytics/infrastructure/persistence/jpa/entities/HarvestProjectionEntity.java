package greendream.cultivatech.platform.analytics.infrastructure.persistence.jpa.entities;

import greendream.cultivatech.platform.shared.infrastructure.persistence.jpa.AuditableAbstractPersistenceEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Date;

@Getter @Setter @NoArgsConstructor @Entity @Table(name="harvest_projections")
public class HarvestProjectionEntity extends AuditableAbstractPersistenceEntity {
    @Column(name="field_id",nullable=false) private Long fieldId;
    @Column(name="crop_type",nullable=false,length=80) private String cropType;
    @Column(name="projected_yield_tons",nullable=false) private Double projectedYieldTons;
    @Column(name="historical_yield_tons",nullable=false) private Double historicalYieldTons;
    @Column(name="projection_period",nullable=false,length=20) private String projectionPeriod;
    @Column(name="confidence_score",nullable=false) private Double confidenceScore;
    @Temporal(TemporalType.TIMESTAMP) @Column(name="generated_at",nullable=false) private Date generatedAt;
}
