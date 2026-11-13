package greendream.cultivatech.platform.profile.application.internal.commandservices;

import greendream.cultivatech.platform.profile.application.commands.CreateFieldCommand;
import greendream.cultivatech.platform.profile.application.commands.DeleteFieldCommand;
import greendream.cultivatech.platform.profile.application.commands.UpdateFieldCommand;
import greendream.cultivatech.platform.profile.application.commandservices.FieldCommandService;
import greendream.cultivatech.platform.profile.application.results.FieldError;
import greendream.cultivatech.platform.profile.application.results.FieldResult;
import greendream.cultivatech.platform.profile.domain.model.aggregates.Field;
import greendream.cultivatech.platform.profile.domain.model.valueobjects.FieldId;
import greendream.cultivatech.platform.profile.domain.model.valueobjects.FieldName;
import greendream.cultivatech.platform.profile.domain.model.valueobjects.FieldSize;
import greendream.cultivatech.platform.profile.domain.model.valueobjects.Latitude;
import greendream.cultivatech.platform.profile.domain.model.valueobjects.Longitude;
import greendream.cultivatech.platform.profile.domain.model.valueobjects.ProfileId;
import greendream.cultivatech.platform.profile.domain.model.valueobjects.SoilType;
import greendream.cultivatech.platform.profile.domain.repositories.FieldRepository;
import greendream.cultivatech.platform.profile.domain.repositories.ProfileRepository;
import greendream.cultivatech.platform.shared.application.result.Result;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of {@link FieldCommandService}.
 */
@Service
public class FieldCommandServiceImpl implements FieldCommandService {

    private final FieldRepository fieldRepository;
    private final ProfileRepository profileRepository;

    public FieldCommandServiceImpl(FieldRepository fieldRepository, ProfileRepository profileRepository) {
        this.fieldRepository = fieldRepository;
        this.profileRepository = profileRepository;
    }

    @Override
    @Transactional
    public Result<FieldResult, FieldError> create(CreateFieldCommand command) {
        if (command == null) {
            return Result.failure(new FieldError("error.command.required"));
        }

        ProfileId profileId;
        try {
            profileId = new ProfileId(command.profileId());
        } catch (IllegalArgumentException exception) {
            return Result.failure(new FieldError(exception.getMessage()));
        }

        if (profileRepository.findById(profileId).isEmpty()) {
            return Result.failure(new FieldError("error.profile.not-found"));
        }

        try {
            Field field = Field.create(
                    profileId,
                    new FieldName(command.name()),
                    new FieldSize(command.sizeM2()),
                    new SoilType(command.soilType()),
                    new Latitude(command.latitude()),
                    new Longitude(command.longitude())
            );

            return Result.success(toResult(fieldRepository.save(field)));
        } catch (IllegalArgumentException exception) {
            return Result.failure(new FieldError(exception.getMessage()));
        }
    }

    @Override
    @Transactional
    public Result<FieldResult, FieldError> update(UpdateFieldCommand command) {
        if (command == null) {
            return Result.failure(new FieldError("error.command.required"));
        }

        FieldId fieldId;
        try {
            fieldId = new FieldId(command.fieldId());
        } catch (IllegalArgumentException exception) {
            return Result.failure(new FieldError(exception.getMessage()));
        }

        Field field = fieldRepository.findById(fieldId).orElse(null);
        if (field == null) {
            return Result.failure(new FieldError("error.field.not-found"));
        }

        try {
            field.changeName(new FieldName(command.name()));
            field.changeSize(new FieldSize(command.sizeM2()));
            field.changeSoilType(new SoilType(command.soilType()));
            field.relocate(new Latitude(command.latitude()), new Longitude(command.longitude()));

            return Result.success(toResult(fieldRepository.update(field)));
        } catch (IllegalArgumentException exception) {
            return Result.failure(new FieldError(exception.getMessage()));
        }
    }

    @Override
    @Transactional
    public Result<Void, FieldError> delete(DeleteFieldCommand command) {
        if (command == null) {
            return Result.failure(new FieldError("error.command.required"));
        }

        FieldId fieldId;
        try {
            fieldId = new FieldId(command.fieldId());
        } catch (IllegalArgumentException exception) {
            return Result.failure(new FieldError(exception.getMessage()));
        }

        if (fieldRepository.findById(fieldId).isEmpty()) {
            return Result.failure(new FieldError("error.field.not-found"));
        }

        try {
            fieldRepository.deleteById(fieldId);
        } catch (IllegalArgumentException exception) {
            return Result.failure(new FieldError(exception.getMessage()));
        }

        return Result.success(null);
    }

    private FieldResult toResult(Field field) {
        Long id = field.id()
                .map(FieldId::value)
                .orElseThrow(() -> new IllegalArgumentException("error.field.id.not-assigned"));

        return new FieldResult(
                id,
                field.profileId().value(),
                field.name().value(),
                field.size().value(),
                field.soilType().value(),
                field.latitude().value(),
                field.longitude().value()
        );
    }
}
