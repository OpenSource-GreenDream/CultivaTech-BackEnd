package greendream.cultivatech.platform.profile.interfaces.rest.controllers;

import greendream.cultivatech.platform.profile.application.commands.RegisterProfileCommand;
import greendream.cultivatech.platform.profile.application.commands.UpdateProfileCommand;
import greendream.cultivatech.platform.profile.application.commandservices.ProfileCommandService;
import greendream.cultivatech.platform.profile.application.queries.GetProfileByUserIdQuery;
import greendream.cultivatech.platform.profile.application.querieservices.ProfileQueryService;
import greendream.cultivatech.platform.profile.application.results.ProfileError;
import greendream.cultivatech.platform.profile.application.results.ProfileResult;
import greendream.cultivatech.platform.profile.interfaces.rest.resources.ProfileResource;
import greendream.cultivatech.platform.profile.interfaces.rest.resources.RegisterProfileRequest;
import greendream.cultivatech.platform.profile.interfaces.rest.resources.UpdateProfileRequest;
import greendream.cultivatech.platform.profile.interfaces.rest.transform.ProfileResourceMapper;
import greendream.cultivatech.platform.shared.application.result.Result;
import greendream.cultivatech.platform.shared.domain.exceptions.ConflictException;
import greendream.cultivatech.platform.shared.domain.exceptions.NotFoundException;
import greendream.cultivatech.platform.shared.infrastructure.security.CurrentUserAccess;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for profile use cases.
 */
@RestController
@RequestMapping("api/v1/users/{userId}/profile")
@Tag(name = "Profiles")
public class ProfileController {

    private final ProfileCommandService profileCommandService;
    private final ProfileQueryService profileQueryService;
    private final ProfileResourceMapper profileResourceMapper;
    private final CurrentUserAccess currentUserAccess;

    public ProfileController(ProfileCommandService profileCommandService,
                             ProfileQueryService profileQueryService,
                             ProfileResourceMapper profileResourceMapper,
                             CurrentUserAccess currentUserAccess) {
        this.profileCommandService = profileCommandService;
        this.profileQueryService = profileQueryService;
        this.profileResourceMapper = profileResourceMapper;
        this.currentUserAccess = currentUserAccess;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Register the profile of a user")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Profile created"),
            @ApiResponse(responseCode = "400", description = "Validation or domain error"),
            @ApiResponse(responseCode = "409", description = "Profile already exists for the user")
    })
    public ResponseEntity<ProfileResource> register(@PathVariable("userId") Long userId,
                                                    @Valid @RequestBody RegisterProfileRequest request) {
        currentUserAccess.requireSelfOrAdmin(userId);

        Result<ProfileResult, ProfileError> result = profileCommandService.register(
                new RegisterProfileCommand(
                        userId,
                        request.fundoName(),
                        request.contactPhone(),
                        request.moistureThreshold(),
                        request.temperatureThreshold()
                )
        );

        return result.fold(
                success -> ResponseEntity.status(HttpStatus.CREATED)
                        .body(profileResourceMapper.toResource(success)),
                error -> {
                    throw toException(error);
                }
        );
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get the profile of a user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profile found"),
            @ApiResponse(responseCode = "400", description = "Validation or domain error"),
            @ApiResponse(responseCode = "404", description = "Profile not found")
    })
    public ResponseEntity<ProfileResource> getByUserId(@PathVariable("userId") Long userId) {
        currentUserAccess.requireSelfOrAdmin(userId);

        Result<ProfileResult, ProfileError> result = profileQueryService.findByUserId(
                new GetProfileByUserIdQuery(userId)
        );

        return result.fold(
                success -> ResponseEntity.ok(profileResourceMapper.toResource(success)),
                error -> {
                    throw toException(error);
                }
        );
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Replace the profile of a user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profile updated"),
            @ApiResponse(responseCode = "400", description = "Validation or domain error"),
            @ApiResponse(responseCode = "404", description = "Profile not found")
    })
    public ResponseEntity<ProfileResource> update(@PathVariable("userId") Long userId,
                                                  @Valid @RequestBody UpdateProfileRequest request) {
        currentUserAccess.requireSelfOrAdmin(userId);

        Result<ProfileResult, ProfileError> result = profileCommandService.update(
                new UpdateProfileCommand(
                        userId,
                        request.fundoName(),
                        request.contactPhone(),
                        request.moistureThreshold(),
                        request.temperatureThreshold()
                )
        );

        return result.fold(
                success -> ResponseEntity.ok(profileResourceMapper.toResource(success)),
                error -> {
                    throw toException(error);
                }
        );
    }

    private RuntimeException toException(ProfileError error) {
        return switch (error.messageKey()) {
            case "error.profile.already-exists" -> new ConflictException(error.messageKey());
            case "error.profile.not-found" -> new NotFoundException(error.messageKey());
            default -> new IllegalArgumentException(error.messageKey());
        };
    }
}
