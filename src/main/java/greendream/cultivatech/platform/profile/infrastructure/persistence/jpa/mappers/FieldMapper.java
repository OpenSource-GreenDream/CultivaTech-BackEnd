package greendream.cultivatech.platform.profile.infrastructure.persistence.jpa.mappers;

import greendream.cultivatech.platform.profile.domain.model.aggregates.Field;
import greendream.cultivatech.platform.profile.domain.model.valueobjects.FieldId;
import greendream.cultivatech.platform.profile.domain.model.valueobjects.FieldName;
import greendream.cultivatech.platform.profile.domain.model.valueobjects.FieldSize;
import greendream.cultivatech.platform.profile.domain.model.valueobjects.Latitude;
import greendream.cultivatech.platform.profile.domain.model.valueobjects.Longitude;
import greendream.cultivatech.platform.profile.domain.model.valueobjects.ProfileId;
import greendream.cultivatech.platform.profile.domain.model.valueobjects.SoilType;
import greendream.cultivatech.platform.profile.infrastructure.persistence.jpa.entities.FieldPersistenceEntity;
import org.springframework.stereotype.Component;

/**
 * Maps between the Field aggregate and its persistence representation.
 */
@Component
public class FieldMapper {

    public FieldPersistenceEntity toNewEntity(Field field) {
        return new FieldPersistenceEntity(
                field.profileId().value(),
                field.name().value(),
                field.size().value(),
                field.soilType().value(),
                field.latitude().value(),
                field.longitude().value()
        );
    }

    public void updateEntity(FieldPersistenceEntity entity, Field field) {
        entity.setName(field.name().value());
        entity.setSizeM2(field.size().value());
        entity.setSoilType(field.soilType().value());
        entity.setLatitude(field.latitude().value());
        entity.setLongitude(field.longitude().value());
    }

    public Field toDomain(FieldPersistenceEntity entity) {
        return Field.reconstitute(
                new FieldId(entity.getId()),
                new ProfileId(entity.getProfileId()),
                new FieldName(entity.getName()),
                new FieldSize(entity.getSizeM2()),
                new SoilType(entity.getSoilType()),
                new Latitude(entity.getLatitude()),
                new Longitude(entity.getLongitude())
        );
    }
}
