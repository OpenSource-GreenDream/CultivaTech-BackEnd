package greendream.cultivatech.platform.profile.domain.model.aggregates;

import greendream.cultivatech.platform.profile.domain.model.events.ProfileRegisteredEvent;
import greendream.cultivatech.platform.profile.domain.model.valueobjects.ContactPhone;
import greendream.cultivatech.platform.profile.domain.model.valueobjects.FundoName;
import greendream.cultivatech.platform.profile.domain.model.valueobjects.MoistureThreshold;
import greendream.cultivatech.platform.profile.domain.model.valueobjects.ProfileId;
import greendream.cultivatech.platform.profile.domain.model.valueobjects.TemperatureThreshold;
import greendream.cultivatech.platform.profile.domain.model.valueobjects.UserId;
import greendream.cultivatech.platform.shared.domain.aggregates.AbstractDomainAggregateRoot;

import java.util.Objects;
import java.util.Optional;

/**
 * Profile aggregate root.
 * Holds the agricultural profile of a user: the fundo name, a contact phone and the
 * moisture and temperature thresholds used by the monitoring features. Cross-context
 * relationships are represented by {@link UserId} and coordinated by the application
 * layer.
 */
public final class Profile extends AbstractDomainAggregateRoot<Profile> {

    private ProfileId id;
    private final UserId userId;
    private FundoName fundoName;
    private ContactPhone contactPhone;
    private MoistureThreshold moistureThreshold;
    private TemperatureThreshold temperatureThreshold;

    private Profile(ProfileId id,
                    UserId userId,
                    FundoName fundoName,
                    ContactPhone contactPhone,
                    MoistureThreshold moistureThreshold,
                    TemperatureThreshold temperatureThreshold) {
        this.id = id;
        this.userId = Objects.requireNonNull(userId, "userId must not be null");
        this.fundoName = Objects.requireNonNull(fundoName, "fundoName must not be null");
        this.contactPhone = Objects.requireNonNull(contactPhone, "contactPhone must not be null");
        this.moistureThreshold = Objects.requireNonNull(moistureThreshold, "moistureThreshold must not be null");
        this.temperatureThreshold = Objects.requireNonNull(temperatureThreshold, "temperatureThreshold must not be null");
    }

    /**
     * Creates a new profile that has not been persisted yet.
     */
    public static Profile register(UserId userId,
                                   FundoName fundoName,
                                   ContactPhone contactPhone,
                                   MoistureThreshold moistureThreshold,
                                   TemperatureThreshold temperatureThreshold) {
        Profile profile = new Profile(null, userId, fundoName, contactPhone, moistureThreshold, temperatureThreshold);
        profile.registerDomainEvent(new ProfileRegisteredEvent(userId));
        return profile;
    }

    /**
     * Rebuilds a persisted profile.
     */
    public static Profile reconstitute(ProfileId id,
                                       UserId userId,
                                       FundoName fundoName,
                                       ContactPhone contactPhone,
                                       MoistureThreshold moistureThreshold,
                                       TemperatureThreshold temperatureThreshold) {
        return new Profile(
                Objects.requireNonNull(id, "id must not be null"),
                userId,
                fundoName,
                contactPhone,
                moistureThreshold,
                temperatureThreshold
        );
    }

    public Optional<ProfileId> id() {
        return Optional.ofNullable(id);
    }

    public UserId userId() {
        return userId;
    }

    public FundoName fundoName() {
        return fundoName;
    }

    public ContactPhone contactPhone() {
        return contactPhone;
    }

    public MoistureThreshold moistureThreshold() {
        return moistureThreshold;
    }

    public TemperatureThreshold temperatureThreshold() {
        return temperatureThreshold;
    }

    /**
     * @return true when this profile belongs to the given user
     */
    public boolean belongsTo(UserId userId) {
        return this.userId.equals(Objects.requireNonNull(userId, "userId must not be null"));
    }

    public void changeFundoName(FundoName newFundoName) {
        this.fundoName = Objects.requireNonNull(newFundoName, "fundoName must not be null");
    }

    public void changeContactPhone(ContactPhone newContactPhone) {
        this.contactPhone = Objects.requireNonNull(newContactPhone, "contactPhone must not be null");
    }

    public void changeMoistureThreshold(MoistureThreshold newMoistureThreshold) {
        this.moistureThreshold = Objects.requireNonNull(newMoistureThreshold, "moistureThreshold must not be null");
    }

    public void changeTemperatureThreshold(TemperatureThreshold newTemperatureThreshold) {
        this.temperatureThreshold = Objects.requireNonNull(newTemperatureThreshold, "temperatureThreshold must not be null");
    }
}
