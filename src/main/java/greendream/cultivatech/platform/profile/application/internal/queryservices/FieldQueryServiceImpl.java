package greendream.cultivatech.platform.profile.application.internal.queryservices;

import greendream.cultivatech.platform.profile.application.queries.GetFieldByIdQuery;
import greendream.cultivatech.platform.profile.application.queries.GetFieldsByProfileIdQuery;
import greendream.cultivatech.platform.profile.application.querieservices.FieldQueryService;
import greendream.cultivatech.platform.profile.application.results.FieldError;
import greendream.cultivatech.platform.profile.application.results.FieldResult;
import greendream.cultivatech.platform.profile.domain.model.aggregates.Field;
import greendream.cultivatech.platform.profile.domain.model.valueobjects.FieldId;
import greendream.cultivatech.platform.profile.domain.model.valueobjects.ProfileId;
import greendream.cultivatech.platform.profile.domain.repositories.FieldRepository;
import greendream.cultivatech.platform.shared.application.result.Result;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementation of {@link FieldQueryService}.
 */
@Service
public class FieldQueryServiceImpl implements FieldQueryService {

    private final FieldRepository fieldRepository;

    public FieldQueryServiceImpl(FieldRepository fieldRepository) {
        this.fieldRepository = fieldRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Result<FieldResult, FieldError> getById(GetFieldByIdQuery query) {
        if (query == null) {
            return Result.failure(new FieldError("error.command.required"));
        }

        FieldId fieldId;
        try {
            fieldId = new FieldId(query.fieldId());
        } catch (IllegalArgumentException exception) {
            return Result.failure(new FieldError(exception.getMessage()));
        }

        Field field = fieldRepository.findById(fieldId).orElse(null);
        if (field == null) {
            return Result.failure(new FieldError("error.field.not-found"));
        }

        return Result.success(toResult(field));
    }

    @Override
    @Transactional(readOnly = true)
    public Result<List<FieldResult>, FieldError> getByProfileId(GetFieldsByProfileIdQuery query) {
        if (query == null) {
            return Result.failure(new FieldError("error.command.required"));
        }

        ProfileId profileId;
        try {
            profileId = new ProfileId(query.profileId());
        } catch (IllegalArgumentException exception) {
            return Result.failure(new FieldError(exception.getMessage()));
        }

        List<FieldResult> fields = fieldRepository.findByProfileId(profileId).stream()
                .map(this::toResult)
                .toList();
        return Result.success(fields);
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
