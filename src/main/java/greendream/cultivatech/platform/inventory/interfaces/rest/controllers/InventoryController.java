package greendream.cultivatech.platform.inventory.interfaces.rest.controllers;

import greendream.cultivatech.platform.inventory.application.commands.DeleteInventoryCommand;
import greendream.cultivatech.platform.inventory.application.commands.RegisterInventoryCommand;
import greendream.cultivatech.platform.inventory.application.commands.UpdateInventoryCommand;
import greendream.cultivatech.platform.inventory.application.commandservices.InventoryCommandService;
import greendream.cultivatech.platform.inventory.application.queries.GetAllInventoriesQuery;
import greendream.cultivatech.platform.inventory.application.queries.GetInventoriesByFieldIdQuery;
import greendream.cultivatech.platform.inventory.application.queries.GetInventoriesByProfileIdQuery;
import greendream.cultivatech.platform.inventory.application.queries.GetInventoryByIdQuery;
import greendream.cultivatech.platform.inventory.application.querieservices.InventoryQueryService;
import greendream.cultivatech.platform.inventory.application.results.InventoryError;
import greendream.cultivatech.platform.inventory.application.results.InventoryResult;
import greendream.cultivatech.platform.inventory.interfaces.rest.resources.InventoryResource;
import greendream.cultivatech.platform.inventory.interfaces.rest.resources.RegisterInventoryRequest;
import greendream.cultivatech.platform.inventory.interfaces.rest.resources.UpdateInventoryRequest;
import greendream.cultivatech.platform.inventory.interfaces.rest.transform.InventoryResourceMapper;
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
 * REST controller for inventory use cases.
 */
@RestController
@RequestMapping("api/v1")
@Tag(name = "Inventories")
public class InventoryController {

    private final InventoryCommandService inventoryCommandService;
    private final InventoryQueryService inventoryQueryService;
    private final InventoryResourceMapper inventoryResourceMapper;

    public InventoryController(InventoryCommandService inventoryCommandService,
                               InventoryQueryService inventoryQueryService,
                               InventoryResourceMapper inventoryResourceMapper) {
        this.inventoryCommandService = inventoryCommandService;
        this.inventoryQueryService = inventoryQueryService;
        this.inventoryResourceMapper = inventoryResourceMapper;
    }

    @GetMapping(value = "/inventories", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "List all inventory entries")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Inventory entries found")
    })
    public ResponseEntity<List<InventoryResource>> getAll() {
        Result<List<InventoryResult>, InventoryError> result =
                inventoryQueryService.getAllInventories(new GetAllInventoriesQuery());

        return result.fold(
                success -> ResponseEntity.ok(inventoryResourceMapper.toResources(success)),
                error -> {
                    throw toException(error);
                }
        );
    }

    @GetMapping(value = "/inventories/{inventoryId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get an inventory entry by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Inventory entry found"),
            @ApiResponse(responseCode = "400", description = "Invalid inventory id"),
            @ApiResponse(responseCode = "404", description = "Inventory entry not found")
    })
    public ResponseEntity<InventoryResource> getById(@PathVariable("inventoryId") Long inventoryId) {
        Result<InventoryResult, InventoryError> result =
                inventoryQueryService.getById(new GetInventoryByIdQuery(inventoryId));

        return result.fold(
                success -> ResponseEntity.ok(inventoryResourceMapper.toResource(success)),
                error -> {
                    throw toException(error);
                }
        );
    }

    @GetMapping(value = "/profiles/{profileId}/inventories", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "List the inventory entries owned by a profile")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Inventory entries found"),
            @ApiResponse(responseCode = "400", description = "Invalid profile id")
    })
    public ResponseEntity<List<InventoryResource>> getByProfileId(@PathVariable("profileId") Long profileId) {
        Result<List<InventoryResult>, InventoryError> result =
                inventoryQueryService.getByProfileId(new GetInventoriesByProfileIdQuery(profileId));

        return result.fold(
                success -> ResponseEntity.ok(inventoryResourceMapper.toResources(success)),
                error -> {
                    throw toException(error);
                }
        );
    }

    @GetMapping(value = "/fields/{fieldId}/inventories", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "List the inventory entries stored on a field")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Inventory entries found"),
            @ApiResponse(responseCode = "400", description = "Invalid field id")
    })
    public ResponseEntity<List<InventoryResource>> getByFieldId(@PathVariable("fieldId") Long fieldId) {
        Result<List<InventoryResult>, InventoryError> result =
                inventoryQueryService.getByFieldId(new GetInventoriesByFieldIdQuery(fieldId));

        return result.fold(
                success -> ResponseEntity.ok(inventoryResourceMapper.toResources(success)),
                error -> {
                    throw toException(error);
                }
        );
    }

    @PostMapping(value = "/profiles/{profileId}/inventories",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Register an inventory entry for a profile")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Inventory entry registered"),
            @ApiResponse(responseCode = "400", description = "Validation or domain error"),
            @ApiResponse(responseCode = "404", description = "Profile not found")
    })
    public ResponseEntity<InventoryResource> register(@PathVariable("profileId") Long profileId,
                                                      @Valid @RequestBody RegisterInventoryRequest request) {
        Result<InventoryResult, InventoryError> result = inventoryCommandService.register(
                new RegisterInventoryCommand(profileId, request.fieldId(), request.stockQuantity())
        );

        return result.fold(
                success -> ResponseEntity.status(HttpStatus.CREATED)
                        .body(inventoryResourceMapper.toResource(success)),
                error -> {
                    throw toException(error);
                }
        );
    }

    @PutMapping(value = "/inventories/{inventoryId}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Update the editable data of an inventory entry")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Inventory entry updated"),
            @ApiResponse(responseCode = "400", description = "Validation or domain error"),
            @ApiResponse(responseCode = "404", description = "Inventory entry not found")
    })
    public ResponseEntity<InventoryResource> update(@PathVariable("inventoryId") Long inventoryId,
                                                    @Valid @RequestBody UpdateInventoryRequest request) {
        Result<InventoryResult, InventoryError> result = inventoryCommandService.update(
                new UpdateInventoryCommand(inventoryId, request.fieldId(), request.stockQuantity())
        );

        return result.fold(
                success -> ResponseEntity.ok(inventoryResourceMapper.toResource(success)),
                error -> {
                    throw toException(error);
                }
        );
    }

    @DeleteMapping("/inventories/{inventoryId}")
    @Operation(summary = "Delete an inventory entry")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Inventory entry deleted"),
            @ApiResponse(responseCode = "400", description = "Invalid inventory id"),
            @ApiResponse(responseCode = "404", description = "Inventory entry not found")
    })
    public ResponseEntity<Void> delete(@PathVariable("inventoryId") Long inventoryId) {
        Result<Void, InventoryError> result =
                inventoryCommandService.delete(new DeleteInventoryCommand(inventoryId));

        return result.fold(
                success -> ResponseEntity.status(HttpStatus.NO_CONTENT).build(),
                error -> {
                    throw toException(error);
                }
        );
    }

    private RuntimeException toException(InventoryError error) {
        return switch (error.messageKey()) {
            case "error.inventory.not-found", "error.profile.not-found" ->
                    new NotFoundException(error.messageKey());
            default -> new IllegalArgumentException(error.messageKey());
        };
    }
}
