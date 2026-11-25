package greendream.cultivatech.platform.analytics.infrastructure.persistence.jpa.entities;

import greendream.cultivatech.platform.shared.infrastructure.persistence.jpa.AuditableAbstractPersistenceEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Date;

@Getter @Setter @NoArgsConstructor @Entity @Table(name="reports")
public class AnalyticsReportEntity extends AuditableAbstractPersistenceEntity {
    @Column(name="device_id",nullable=false) private Long deviceId;
    @Column(name="parameter",nullable=false,length=30) private String parameter;
    @Column(name="unit",nullable=false,length=10) private String unit;
    @Temporal(TemporalType.TIMESTAMP) @Column(name="period_start",nullable=false) private Date periodStart;
    @Temporal(TemporalType.TIMESTAMP) @Column(name="period_end",nullable=false) private Date periodEnd;
    @Column(name="sample_count",nullable=false) private Integer sampleCount;
    @Temporal(TemporalType.TIMESTAMP) @Column(name="generated_at",nullable=false) private Date generatedAt;
    @Column(name="mean_value",nullable=false) private Double meanValue;
    @Column(name="variance",nullable=false) private Double variance;
    @Column(name="standard_deviation",nullable=false) private Double standardDeviation;
    @Column(name="technical_interpretation",length=500) private String technicalInterpretation;
}
