package greendream.cultivatech.platform.iam.interfaces.rest.controllers;

import greendream.cultivatech.platform.iam.application.commands.SignUpCommand;
import greendream.cultivatech.platform.iam.application.commandservices.AuthenticationCommandService;
import greendream.cultivatech.platform.iam.application.results.SignUpError;
import greendream.cultivatech.platform.iam.application.results.SignUpResult;
import greendream.cultivatech.platform.iam.interfaces.rest.resources.SignUpRequest;
import greendream.cultivatech.platform.iam.interfaces.rest.resources.UserResource;
import greendream.cultivatech.platform.iam.interfaces.rest.transform.UserResourceMapper;
import greendream.cultivatech.platform.shared.application.result.Result;
import greendream.cultivatech.platform.shared.domain.exceptions.ConflictException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for authentication use cases.
 */
@RestController
@RequestMapping("api/v1/authentication")
@Tag(name = "Authentication")
public class AuthenticationController {

    private final AuthenticationCommandService authenticationCommandService;
    private final UserResourceMapper userResourceMapper;

    public AuthenticationController(AuthenticationCommandService authenticationCommandService,
                                    UserResourceMapper userResourceMapper) {
        this.authenticationCommandService = authenticationCommandService;
        this.userResourceMapper = userResourceMapper;
    }

    @PostMapping(value = "/sign-up", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Register a new farmer")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User created"),
            @ApiResponse(responseCode = "400", description = "Validation or domain error"),
            @ApiResponse(responseCode = "409", description = "Email already registered")
    })
    public ResponseEntity<UserResource> signUp(@Valid @RequestBody SignUpRequest request) {
        Result<SignUpResult, SignUpError> result = authenticationCommandService.signUp(
                new SignUpCommand(request.email(), request.password())
        );

        return result.fold(
                success -> ResponseEntity.status(HttpStatus.CREATED)
                        .body(userResourceMapper.toResource(success)),
                error -> {
                    if ("error.user.email.already-exists".equals(error.messageKey())) {
                        throw new ConflictException(error.messageKey());
                    }
                    throw new IllegalArgumentException(error.messageKey());
                }
        );
    }
}
