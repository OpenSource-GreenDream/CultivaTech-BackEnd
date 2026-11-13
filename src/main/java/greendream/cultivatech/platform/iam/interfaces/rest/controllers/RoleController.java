package greendream.cultivatech.platform.iam.interfaces.rest.controllers;

import greendream.cultivatech.platform.iam.application.commands.CreateRoleCommand;
import greendream.cultivatech.platform.iam.application.commands.DeleteRoleCommand;
import greendream.cultivatech.platform.iam.application.commands.UpdateRoleCommand;
import greendream.cultivatech.platform.iam.application.commandservices.RoleCommandService;
import greendream.cultivatech.platform.iam.application.queries.GetAllRolesQuery;
import greendream.cultivatech.platform.iam.application.queries.GetRoleByIdQuery;
import greendream.cultivatech.platform.iam.application.querieservices.RoleQueryService;
import greendream.cultivatech.platform.iam.application.results.RoleError;
import greendream.cultivatech.platform.iam.application.results.RoleResult;
import greendream.cultivatech.platform.iam.interfaces.rest.resources.CreateRoleRequest;
import greendream.cultivatech.platform.iam.interfaces.rest.resources.RoleResource;
import greendream.cultivatech.platform.iam.interfaces.rest.resources.UpdateRoleRequest;
import greendream.cultivatech.platform.iam.interfaces.rest.transform.RoleResourceMapper;
import greendream.cultivatech.platform.shared.application.result.Result;
import greendream.cultivatech.platform.shared.domain.exceptions.ConflictException;
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
 * REST controller for role administration use cases. All routes are restricted to
 * administrators by the global security configuration.
 */
@RestController
@RequestMapping("api/v1/roles")
@Tag(name = "Roles")
public class RoleController {

    private final RoleQueryService roleQueryService;
    private final RoleCommandService roleCommandService;
    private final RoleResourceMapper roleResourceMapper;

    public RoleController(RoleQueryService roleQueryService,
                          RoleCommandService roleCommandService,
                          RoleResourceMapper roleResourceMapper) {
        this.roleQueryService = roleQueryService;
        this.roleCommandService = roleCommandService;
        this.roleResourceMapper = roleResourceMapper;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "List all roles")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Roles found"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid token"),
            @ApiResponse(responseCode = "403", description = "Insufficient role")
    })
    public ResponseEntity<List<RoleResource>> getAll() {
        Result<List<RoleResult>, RoleError> result = roleQueryService.getAllRoles(new GetAllRolesQuery());

        return result.fold(
                success -> ResponseEntity.ok(roleResourceMapper.toResources(success)),
                error -> {
                    throw toException(error);
                }
        );
    }

    @GetMapping(value = "/{roleId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get a role by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Role found"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid token"),
            @ApiResponse(responseCode = "403", description = "Insufficient role"),
            @ApiResponse(responseCode = "404", description = "Role not found")
    })
    public ResponseEntity<RoleResource> getById(@PathVariable("roleId") Long roleId) {
        Result<RoleResult, RoleError> result = roleQueryService.getRoleById(new GetRoleByIdQuery(roleId));

        return result.fold(
                success -> ResponseEntity.ok(roleResourceMapper.toResource(success)),
                error -> {
                    throw toException(error);
                }
        );
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Create a role")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Role created"),
            @ApiResponse(responseCode = "400", description = "Validation or domain error"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid token"),
            @ApiResponse(responseCode = "403", description = "Insufficient role"),
            @ApiResponse(responseCode = "409", description = "Role name already exists")
    })
    public ResponseEntity<RoleResource> create(@Valid @RequestBody CreateRoleRequest request) {
        Result<RoleResult, RoleError> result = roleCommandService.create(
                new CreateRoleCommand(request.name(), request.description())
        );

        return result.fold(
                success -> ResponseEntity.status(HttpStatus.CREATED)
                        .body(roleResourceMapper.toResource(success)),
                error -> {
                    throw toException(error);
                }
        );
    }

    @PutMapping(value = "/{roleId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Update the description of a role")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Role updated"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid token"),
            @ApiResponse(responseCode = "403", description = "Insufficient role"),
            @ApiResponse(responseCode = "404", description = "Role not found")
    })
    public ResponseEntity<RoleResource> update(@PathVariable("roleId") Long roleId,
                                               @Valid @RequestBody UpdateRoleRequest request) {
        Result<RoleResult, RoleError> result = roleCommandService.update(
                new UpdateRoleCommand(roleId, request.description())
        );

        return result.fold(
                success -> ResponseEntity.ok(roleResourceMapper.toResource(success)),
                error -> {
                    throw toException(error);
                }
        );
    }

    @DeleteMapping("/{roleId}")
    @Operation(summary = "Delete a role")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Role deleted"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid token"),
            @ApiResponse(responseCode = "403", description = "Insufficient role"),
            @ApiResponse(responseCode = "404", description = "Role not found"),
            @ApiResponse(responseCode = "409", description = "Role is still assigned to users")
    })
    public ResponseEntity<Void> delete(@PathVariable("roleId") Long roleId) {
        Result<Void, RoleError> result = roleCommandService.delete(new DeleteRoleCommand(roleId));

        return result.fold(
                success -> ResponseEntity.status(HttpStatus.NO_CONTENT).build(),
                error -> {
                    throw toException(error);
                }
        );
    }

    private RuntimeException toException(RoleError error) {
        return switch (error.messageKey()) {
            case "error.role.not-found" -> new NotFoundException(error.messageKey());
            case "error.role.name.already-exists", "error.role.in-use" -> new ConflictException(error.messageKey());
            default -> new IllegalArgumentException(error.messageKey());
        };
    }
}
