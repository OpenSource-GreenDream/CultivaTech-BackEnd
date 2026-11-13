package greendream.cultivatech.platform.iam.interfaces.rest.controllers;

import greendream.cultivatech.platform.iam.application.commands.SignInCommand;
import greendream.cultivatech.platform.iam.application.commands.SignUpCommand;
import greendream.cultivatech.platform.iam.application.commandservices.AuthenticationCommandService;
import greendream.cultivatech.platform.iam.application.results.SignInError;
import greendream.cultivatech.platform.iam.application.results.SignInResult;
import greendream.cultivatech.platform.iam.application.results.SignUpError;
import greendream.cultivatech.platform.iam.application.results.SignUpResult;
import greendream.cultivatech.platform.iam.interfaces.rest.resources.SignInRequest;
import greendream.cultivatech.platform.iam.interfaces.rest.resources.SignInResource;
import greendream.cultivatech.platform.iam.interfaces.rest.resources.SignUpRequest;
import greendream.cultivatech.platform.iam.interfaces.rest.resources.UserResource;
import greendream.cultivatech.platform.iam.interfaces.rest.transform.SignInResourceMapper;
import greendream.cultivatech.platform.iam.interfaces.rest.transform.UserResourceMapper;
import greendream.cultivatech.platform.shared.application.result.Result;
import greendream.cultivatech.platform.shared.domain.exceptions.ConflictException;
import greendream.cultivatech.platform.shared.domain.exceptions.UnauthorizedException;
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
    private final SignInResourceMapper signInResourceMapper;

    public AuthenticationController(AuthenticationCommandService authenticationCommandService,
                                    UserResourceMapper userResourceMapper,
                                    SignInResourceMapper signInResourceMapper) {
        this.authenticationCommandService = authenticationCommandService;
        this.userResourceMapper = userResourceMapper;
        this.signInResourceMapper = signInResourceMapper;
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

    @PostMapping(value = "/sign-in", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Authenticate a user and issue an access token")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Authenticated"),
            @ApiResponse(responseCode = "400", description = "Validation or domain error"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    public ResponseEntity<SignInResource> signIn(@Valid @RequestBody SignInRequest request) {
        Result<SignInResult, SignInError> result = authenticationCommandService.signIn(
                new SignInCommand(request.email(), request.password())
        );

        return result.fold(
                success -> ResponseEntity.ok(signInResourceMapper.toResource(success)),
                error -> {
                    if ("error.authentication.invalid-credentials".equals(error.messageKey())) {
                        throw new UnauthorizedException(error.messageKey());
                    }
                    throw new IllegalArgumentException(error.messageKey());
                }
        );
    }
}
