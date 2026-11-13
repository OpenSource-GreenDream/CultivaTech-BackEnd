package greendream.cultivatech.platform.profile.domain.model.aggregates;

import greendream.cultivatech.platform.profile.domain.model.valueobjects.FieldId;
import greendream.cultivatech.platform.profile.domain.model.valueobjects.FieldName;
import greendream.cultivatech.platform.profile.domain.model.valueobjects.FieldSize;
import greendream.cultivatech.platform.profile.domain.model.valueobjects.Latitude;
import greendream.cultivatech.platform.profile.domain.model.valueobjects.Longitude;
import greendream.cultivatech.platform.profile.domain.model.valueobjects.ProfileId;
import greendream.cultivatech.platform.profile.domain.model.valueobjects.SoilType;
import greendream.cultivatech.platform.shared.domain.aggregates.AbstractDomainAggregateRoot;

import java.util.Objects;
import java.util.Optional;

/**
 * Field aggregate root.
 * Represents a plot of land that belongs to a profile. The owning profile is
 * referenced by {@link ProfileId} so the aggregate stays inside the Profile bounded
 * context, and other contexts refer to a field through its own identity.
 */
public final class Field extends AbstractDomainAggregateRoot<Field> {

    private FieldId id;
    private final ProfileId profileId;
    private FieldName name;
    private FieldSize size;
    private SoilType soilType;
    private Latitude latitude;
    private Longitude longitude;

    private Field(FieldId id,
                  ProfileId profileId,
                  FieldName name,
                  FieldSize size,
                  SoilType soilType,
                  Latitude latitude,
                  Longitude longitude) {
        this.id = id;
        this.profileId = Objects.requireNonNull(profileId, "profileId must not be null");
        this.name = Objects.requireNonNull(name, "name must not be null");
        this.size = Objects.requireNonNull(size, "size must not be null");
        this.soilType = Objects.requireNonNull(soilType, "soilType must not be null");
        this.latitude = Objects.requireNonNull(latitude, "latitude must not be null");
        this.longitude = Objects.requireNonNull(longitude, "longitude must not be null");
    }

    /**
     * Creates a new field that has not been persisted yet.
     */
    public static Field create(ProfileId profileId,
                               FieldName name,
                               FieldSize size,
                               SoilType soilType,
                               Latitude latitude,
                               Longitude longitude) {
        return new Field(null, profileId, name, size, soilType, latitude, longitude);
    }

    /**
     * Rebuilds a persisted field.
     */
    public static Field reconstitute(FieldId id,
                                     ProfileId profileId,
                                     FieldName name,
                                     FieldSize size,
                                     SoilType soilType,
                                     Latitude latitude,
                                     Longitude longitude) {
        return new Field(
                Objects.requireNonNull(id, "id must not be null"),
                profileId,
                name,
                size,
                soilType,
                latitude,
                longitude
        );
    }

    public Optional<FieldId> id() {
        return Optional.ofNullable(id);
    }

    public ProfileId profileId() {
        return profileId;
    }

    public FieldName name() {
        return name;
    }

    public FieldSize size() {
        return size;
    }

    public SoilType soilType() {
        return soilType;
    }

    public Latitude latitude() {
        return latitude;
    }

    public Longitude longitude() {
        return longitude;
    }

    /**
     * @return true when this field belongs to the given profile
     */
    public boolean belongsTo(ProfileId profileId) {
        return this.profileId.equals(Objects.requireNonNull(profileId, "profileId must not be null"));
    }

    public void changeName(FieldName newName) {
        this.name = Objects.requireNonNull(newName, "name must not be null");
    }

    public void changeSize(FieldSize newSize) {
        this.size = Objects.requireNonNull(newSize, "size must not be null");
    }

    public void changeSoilType(SoilType newSoilType) {
        this.soilType = Objects.requireNonNull(newSoilType, "soilType must not be null");
    }

    public void relocate(Latitude newLatitude, Longitude newLongitude) {
        this.latitude = Objects.requireNonNull(newLatitude, "latitude must not be null");
        this.longitude = Objects.requireNonNull(newLongitude, "longitude must not be null");
    }
}
