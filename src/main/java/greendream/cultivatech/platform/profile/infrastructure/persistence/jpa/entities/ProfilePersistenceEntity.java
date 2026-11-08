package greendream.cultivatech.platform.profile.infrastructure.persistence.jpa.entities;

import greendream.cultivatech.platform.shared.infrastructure.persistence.jpa.AuditableAbstractPersistenceEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

/**
 * Persistence representation of the profiles table.
 * The owning user is stored as a plain identifier to keep the Profile bounded
 * context independent of the IAM schema. Uniqueness guarantees one profile per
 * user.
 */
@Setter
@Getter
@Entity
@Table(
        name = "profiles",
        uniqueConstraints = @UniqueConstraint(name = "uk_profiles_user_id", columnNames = "user_id")
)
public class ProfilePersistenceEntity extends AuditableAbstractPersistenceEntity {

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "fundo_name", nullable = false, length = 120)
    private String fundoName;

    @Column(name = "contact_phone", nullable = false, length = 20)
    private String contactPhone;

    @Column(name = "moisture_threshold", nullable = false)
    private double moistureThreshold;

    @Column(name = "temperature_threshold", nullable = false)
    private double temperatureThreshold;

    protected ProfilePersistenceEntity() {
    }

    public ProfilePersistenceEntity(Long userId,
                                    String fundoName,
                                    String contactPhone,
                                    double moistureThreshold,
                                    double temperatureThreshold) {
        this.userId = userId;
        this.fundoName = fundoName;
        this.contactPhone = contactPhone;
        this.moistureThreshold = moistureThreshold;
        this.temperatureThreshold = temperatureThreshold;
    }
}
