package greendream.cultivatech.platform.profile.infrastructure.persistence.jpa.mappers;

import greendream.cultivatech.platform.profile.domain.model.aggregates.Profile;
import greendream.cultivatech.platform.profile.domain.model.valueobjects.ContactPhone;
import greendream.cultivatech.platform.profile.domain.model.valueobjects.FundoName;
import greendream.cultivatech.platform.profile.domain.model.valueobjects.MoistureThreshold;
import greendream.cultivatech.platform.profile.domain.model.valueobjects.ProfileId;
import greendream.cultivatech.platform.profile.domain.model.valueobjects.TemperatureThreshold;
import greendream.cultivatech.platform.profile.domain.model.valueobjects.UserId;
import greendream.cultivatech.platform.profile.infrastructure.persistence.jpa.entities.ProfilePersistenceEntity;
import org.springframework.stereotype.Component;

/**
 * Maps between the Profile aggregate and its persistence representation.
 */
@Component
public class ProfileMapper {

    public ProfilePersistenceEntity toNewEntity(Profile profile) {
        return new ProfilePersistenceEntity(
                profile.userId().value(),
                profile.fundoName().value(),
                profile.contactPhone().value(),
                profile.moistureThreshold().value(),
                profile.temperatureThreshold().value()
        );
    }

    public void updateEntity(ProfilePersistenceEntity entity, Profile profile) {
        entity.setFundoName(profile.fundoName().value());
        entity.setContactPhone(profile.contactPhone().value());
        entity.setMoistureThreshold(profile.moistureThreshold().value());
        entity.setTemperatureThreshold(profile.temperatureThreshold().value());
    }

    public Profile toDomain(ProfilePersistenceEntity entity) {
        return Profile.reconstitute(
                new ProfileId(entity.getId()),
                new UserId(entity.getUserId()),
                new FundoName(entity.getFundoName()),
                new ContactPhone(entity.getContactPhone()),
                new MoistureThreshold(entity.getMoistureThreshold()),
                new TemperatureThreshold(entity.getTemperatureThreshold())
        );
    }
}
