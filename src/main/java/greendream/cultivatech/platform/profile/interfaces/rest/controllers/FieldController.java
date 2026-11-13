package greendream.cultivatech.platform.profile.interfaces.rest.controllers;

import greendream.cultivatech.platform.profile.application.commands.CreateFieldCommand;
import greendream.cultivatech.platform.profile.application.commands.DeleteFieldCommand;
import greendream.cultivatech.platform.profile.application.commands.UpdateFieldCommand;
import greendream.cultivatech.platform.profile.application.commandservices.FieldCommandService;
import greendream.cultivatech.platform.profile.application.queries.GetFieldByIdQuery;
import greendream.cultivatech.platform.profile.application.queries.GetFieldsByProfileIdQuery;
import greendream.cultivatech.platform.profile.application.querieservices.FieldQueryService;
import greendream.cultivatech.platform.profile.application.results.FieldError;
import greendream.cultivatech.platform.profile.application.results.FieldResult;
import greendream.cultivatech.platform.profile.interfaces.rest.resources.CreateFieldRequest;
import greendream.cultivatech.platform.profile.interfaces.rest.resources.FieldResource;
import greendream.cultivatech.platform.profile.interfaces.rest.resources.UpdateFieldRequest;
import greendream.cultivatech.platform.profile.interfaces.rest.transform.FieldResourceMapper;
import greendream.cultivatech.platform.shared.application.result.Result;
import greendream.cultivatech.platform.shared.domain.exceptions.NotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for field use cases.
 */
@RestController
@RequestMapping("api/v1")
@Tag(name = "Fields")
public class FieldController {

    private final FieldCommandService fieldCommandService;
    private final FieldQueryService fieldQueryService;
    private final FieldResourceMapper fieldResourceMapper;

    public FieldController(FieldCommandService fieldCommandService,
                           FieldQueryService fieldQueryService,
                           FieldResourceMapper fieldResourceMapper) {
        this.fieldCommandService = fieldCommandService;
        this.fieldQueryService = fieldQueryService;
        this.fieldResourceMapper = fieldResourceMapper;
    }

    @GetMapping(value = "/profiles/{profileId}/fields", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "List the fields owned by a profile")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Fields found"),
            @ApiResponse(responseCode = "400", description = "Invalid profile id")
    })
    public ResponseEntity<List<FieldResource>> getByProfileId(@PathVariable("profileId") Long profileId) {
        Result<List<FieldResult>, FieldError> result = fieldQueryService.getByProfileId(
                new GetFieldsByProfileIdQuery(profileId)
        );

        return result.fold(
                success -> ResponseEntity.ok(fieldResourceMapper.toResources(success)),
                error -> {
                    throw toException(error);
                }
        );
    }

    @GetMapping(value = "/fields/{fieldId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get a field by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Field found"),
            @ApiResponse(responseCode = "400", description = "Invalid field id"),
            @ApiResponse(responseCode = "404", description = "Field not found")
    })
    public ResponseEntity<FieldResource> getById(@PathVariable("fieldId") Long fieldId) {
        Result<FieldResult, FieldError> result = fieldQueryService.getById(new GetFieldByIdQuery(fieldId));

        return result.fold(
                success -> ResponseEntity.ok(fieldResourceMapper.toResource(success)),
                error -> {
                    throw toException(error);
                }
        );
    }

    @PostMapping(value = "/profiles/{profileId}/fields",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Create a field owned by a profile")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Field created"),
            @ApiResponse(responseCode = "400", description = "Validation or domain error"),
            @ApiResponse(responseCode = "404", description = "Profile not found")
    })
    public ResponseEntity<FieldResource> create(@PathVariable("profileId") Long profileId,
                                                @Valid @RequestBody CreateFieldRequest request) {
        Result<FieldResult, FieldError> result = fieldCommandService.create(
                new CreateFieldCommand(
                        profileId,
                        request.name(),
                        request.sizeM2(),
                        request.soilType(),
                        request.latitude(),
                        request.longitude()
                )
        );

        return result.fold(
                success -> ResponseEntity.status(HttpStatus.CREATED)
                        .body(fieldResourceMapper.toResource(success)),
                error -> {
                    throw toException(error);
                }
        );
    }

    @PutMapping(value = "/fields/{fieldId}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Replace the editable data of a field")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Field updated"),
            @ApiResponse(responseCode = "400", description = "Validation or domain error"),
            @ApiResponse(responseCode = "404", description = "Field not found")
    })
    public ResponseEntity<FieldResource> update(@PathVariable("fieldId") Long fieldId,
                                                @Valid @RequestBody UpdateFieldRequest request) {
        Result<FieldResult, FieldError> result = fieldCommandService.update(
                new UpdateFieldCommand(
                        fieldId,
                        request.name(),
                        request.sizeM2(),
                        request.soilType(),
                        request.latitude(),
                        request.longitude()
                )
        );

        return result.fold(
                success -> ResponseEntity.ok(fieldResourceMapper.toResource(success)),
                error -> {
                    throw toException(error);
                }
        );
    }

    @DeleteMapping("/fields/{fieldId}")
    @Operation(summary = "Delete a field")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Field deleted"),
            @ApiResponse(responseCode = "400", description = "Invalid field id"),
            @ApiResponse(responseCode = "404", description = "Field not found")
    })
    public ResponseEntity<Void> delete(@PathVariable("fieldId") Long fieldId) {
        Result<Void, FieldError> result = fieldCommandService.delete(new DeleteFieldCommand(fieldId));

        return result.fold(
                success -> ResponseEntity.status(HttpStatus.NO_CONTENT).build(),
                error -> {
                    throw toException(error);
                }
        );
    }

    private RuntimeException toException(FieldError error) {
        return switch (error.messageKey()) {
            case "error.field.not-found", "error.profile.not-found" -> new NotFoundException(error.messageKey());
            default -> new IllegalArgumentException(error.messageKey());
        };
    }
}
