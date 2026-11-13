package greendream.cultivatech.platform.profile.infrastructure.persistence.jpa.entities;

import greendream.cultivatech.platform.shared.infrastructure.persistence.jpa.AuditableAbstractPersistenceEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * Persistence representation of the fields table.
 * The owning profile is stored as a plain identifier because the aggregate references
 * it by {@code ProfileId}; JPA associations between aggregates would leak the
 * persistence graph into the domain. The name is intentionally not unique: a profile
 * may hold several fields with the same name.
 */
@Setter
@Getter
@Entity
@Table(
        name = "fields",
        indexes = @Index(name = "idx_fields_profile_id", columnList = "profile_id")
)
public class FieldPersistenceEntity extends AuditableAbstractPersistenceEntity {

    @Column(name = "profile_id", nullable = false)
    private Long profileId;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "size_m2", nullable = false)
    private double sizeM2;

    @Column(name = "soil_type", nullable = false, length = 50)
    private String soilType;

    @Column(name = "latitude", nullable = false)
    private double latitude;

    @Column(name = "longitude", nullable = false)
    private double longitude;

    protected FieldPersistenceEntity() {
    }

    public FieldPersistenceEntity(Long profileId,
                                  String name,
                                  double sizeM2,
                                  String soilType,
                                  double latitude,
                                  double longitude) {
        this.profileId = profileId;
        this.name = name;
        this.sizeM2 = sizeM2;
        this.soilType = soilType;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
