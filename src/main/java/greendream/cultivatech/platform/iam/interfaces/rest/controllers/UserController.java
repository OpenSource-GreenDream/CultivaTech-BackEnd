package greendream.cultivatech.platform.iam.interfaces.rest.controllers;

import greendream.cultivatech.platform.iam.application.commands.DeleteUserCommand;
import greendream.cultivatech.platform.iam.application.commands.UpdateUserCommand;
import greendream.cultivatech.platform.iam.application.commandservices.UserCommandService;
import greendream.cultivatech.platform.iam.application.queries.GetAllUsersQuery;
import greendream.cultivatech.platform.iam.application.queries.GetUserByIdQuery;
import greendream.cultivatech.platform.iam.application.querieservices.UserQueryService;
import greendream.cultivatech.platform.iam.application.results.UserError;
import greendream.cultivatech.platform.iam.application.results.UserResult;
import greendream.cultivatech.platform.iam.interfaces.rest.resources.UpdateUserRequest;
import greendream.cultivatech.platform.iam.interfaces.rest.resources.UserSummaryResource;
import greendream.cultivatech.platform.iam.interfaces.rest.transform.UserSummaryResourceMapper;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for user administration use cases.
 */
@RestController
@RequestMapping("api/v1/users")
@Tag(name = "Users")
public class UserController {

    private final UserQueryService userQueryService;
    private final UserCommandService userCommandService;
    private final UserSummaryResourceMapper userSummaryResourceMapper;
    private final CurrentUserAccess currentUserAccess;

    public UserController(UserQueryService userQueryService,
                          UserCommandService userCommandService,
                          UserSummaryResourceMapper userSummaryResourceMapper,
                          CurrentUserAccess currentUserAccess) {
        this.userQueryService = userQueryService;
        this.userCommandService = userCommandService;
        this.userSummaryResourceMapper = userSummaryResourceMapper;
        this.currentUserAccess = currentUserAccess;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "List all users")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Users found"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid token"),
            @ApiResponse(responseCode = "403", description = "Insufficient role")
    })
    public ResponseEntity<List<UserSummaryResource>> getAll() {
        Result<List<UserResult>, UserError> result = userQueryService.getAllUsers(new GetAllUsersQuery());

        return result.fold(
                success -> ResponseEntity.ok(userSummaryResourceMapper.toResources(success)),
                error -> {
                    throw toException(error);
                }
        );
    }

    @GetMapping(value = "/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get a user by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User found"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid token"),
            @ApiResponse(responseCode = "403", description = "Not the owner and not an administrator"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<UserSummaryResource> getById(@PathVariable("userId") Long userId) {
        currentUserAccess.requireSelfOrAdmin(userId);

        Result<UserResult, UserError> result = userQueryService.getUserById(new GetUserByIdQuery(userId));

        return result.fold(
                success -> ResponseEntity.ok(userSummaryResourceMapper.toResource(success)),
                error -> {
                    throw toException(error);
                }
        );
    }

    @PutMapping(value = "/{userId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Update a user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User updated"),
            @ApiResponse(responseCode = "400", description = "Validation or domain error"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid token"),
            @ApiResponse(responseCode = "403", description = "Not the owner and not an administrator"),
            @ApiResponse(responseCode = "404", description = "User or role not found"),
            @ApiResponse(responseCode = "409", description = "Email already registered")
    })
    public ResponseEntity<UserSummaryResource> update(@PathVariable("userId") Long userId,
                                                      @Valid @RequestBody UpdateUserRequest request) {
        currentUserAccess.requireSelfOrAdmin(userId);

        Result<UserResult, UserError> result = userCommandService.update(
                new UpdateUserCommand(userId, request.email(), request.password(), request.roleIds())
        );

        return result.fold(
                success -> ResponseEntity.ok(userSummaryResourceMapper.toResource(success)),
                error -> {
                    throw toException(error);
                }
        );
    }

    @DeleteMapping("/{userId}")
    @Operation(summary = "Delete a user")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "User deleted"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid token"),
            @ApiResponse(responseCode = "403", description = "Insufficient role"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<Void> delete(@PathVariable("userId") Long userId) {
        Result<Void, UserError> result = userCommandService.delete(new DeleteUserCommand(userId));

        return result.fold(
                success -> ResponseEntity.status(HttpStatus.NO_CONTENT).build(),
                error -> {
                    throw toException(error);
                }
        );
    }

    private RuntimeException toException(UserError error) {
        return switch (error.messageKey()) {
            case "error.user.not-found", "error.role.not-found" -> new NotFoundException(error.messageKey());
            case "error.user.email.already-exists" -> new ConflictException(error.messageKey());
            default -> new IllegalArgumentException(error.messageKey());
        };
    }
}
