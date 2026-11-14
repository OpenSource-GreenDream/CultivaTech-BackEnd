package greendream.cultivatech.platform.monitoring.interfaces.rest.controllers;

import greendream.cultivatech.platform.monitoring.application.commands.DeleteDeviceCommand;
import greendream.cultivatech.platform.monitoring.application.commands.RegisterDeviceCommand;
import greendream.cultivatech.platform.monitoring.application.commands.UpdateDeviceCommand;
import greendream.cultivatech.platform.monitoring.application.commandservices.DeviceCommandService;
import greendream.cultivatech.platform.monitoring.application.queries.GetAllDevicesQuery;
import greendream.cultivatech.platform.monitoring.application.queries.GetDeviceByIdQuery;
import greendream.cultivatech.platform.monitoring.application.queries.GetDevicesByFieldIdQuery;
import greendream.cultivatech.platform.monitoring.application.querieservices.DeviceQueryService;
import greendream.cultivatech.platform.monitoring.application.results.DeviceError;
import greendream.cultivatech.platform.monitoring.application.results.DeviceResult;
import greendream.cultivatech.platform.monitoring.interfaces.rest.resources.DeviceResource;
import greendream.cultivatech.platform.monitoring.interfaces.rest.resources.RegisterDeviceRequest;
import greendream.cultivatech.platform.monitoring.interfaces.rest.resources.UpdateDeviceRequest;
import greendream.cultivatech.platform.monitoring.interfaces.rest.transform.DeviceResourceMapper;
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
 * REST controller for device use cases.
 */
@RestController
@RequestMapping("api/v1")
@Tag(name = "Devices")
public class DeviceController {

    private final DeviceCommandService deviceCommandService;
    private final DeviceQueryService deviceQueryService;
    private final DeviceResourceMapper deviceResourceMapper;

    public DeviceController(DeviceCommandService deviceCommandService,
                            DeviceQueryService deviceQueryService,
                            DeviceResourceMapper deviceResourceMapper) {
        this.deviceCommandService = deviceCommandService;
        this.deviceQueryService = deviceQueryService;
        this.deviceResourceMapper = deviceResourceMapper;
    }

    @GetMapping(value = "/devices", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "List all devices")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Devices found")
    })
    public ResponseEntity<List<DeviceResource>> getAll() {
        Result<List<DeviceResult>, DeviceError> result = deviceQueryService.getAllDevices(new GetAllDevicesQuery());

        return result.fold(
                success -> ResponseEntity.ok(deviceResourceMapper.toResources(success)),
                error -> {
                    throw toException(error);
                }
        );
    }

    @GetMapping(value = "/devices/{deviceId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get a device by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Device found"),
            @ApiResponse(responseCode = "400", description = "Invalid device id"),
            @ApiResponse(responseCode = "404", description = "Device not found")
    })
    public ResponseEntity<DeviceResource> getById(@PathVariable("deviceId") Long deviceId) {
        Result<DeviceResult, DeviceError> result = deviceQueryService.getById(new GetDeviceByIdQuery(deviceId));

        return result.fold(
                success -> ResponseEntity.ok(deviceResourceMapper.toResource(success)),
                error -> {
                    throw toException(error);
                }
        );
    }

    @GetMapping(value = "/fields/{fieldId}/devices", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "List the devices installed on a field")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Devices found"),
            @ApiResponse(responseCode = "400", description = "Invalid field id")
    })
    public ResponseEntity<List<DeviceResource>> getByFieldId(@PathVariable("fieldId") Long fieldId) {
        Result<List<DeviceResult>, DeviceError> result = deviceQueryService.getByFieldId(
                new GetDevicesByFieldIdQuery(fieldId)
        );

        return result.fold(
                success -> ResponseEntity.ok(deviceResourceMapper.toResources(success)),
                error -> {
                    throw toException(error);
                }
        );
    }

    @PostMapping(value = "/fields/{fieldId}/devices",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Register a device on a field")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Device registered"),
            @ApiResponse(responseCode = "400", description = "Validation or domain error"),
            @ApiResponse(responseCode = "409", description = "MAC address already registered")
    })
    public ResponseEntity<DeviceResource> register(@PathVariable("fieldId") Long fieldId,
                                                   @Valid @RequestBody RegisterDeviceRequest request) {
        Result<DeviceResult, DeviceError> result = deviceCommandService.register(
                new RegisterDeviceCommand(fieldId, request.macAddress(), request.status())
        );

        return result.fold(
                success -> ResponseEntity.status(HttpStatus.CREATED)
                        .body(deviceResourceMapper.toResource(success)),
                error -> {
                    throw toException(error);
                }
        );
    }

    @PutMapping(value = "/devices/{deviceId}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Update the operational status of a device")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Device updated"),
            @ApiResponse(responseCode = "400", description = "Validation or domain error"),
            @ApiResponse(responseCode = "404", description = "Device not found")
    })
    public ResponseEntity<DeviceResource> update(@PathVariable("deviceId") Long deviceId,
                                                 @Valid @RequestBody UpdateDeviceRequest request) {
        Result<DeviceResult, DeviceError> result = deviceCommandService.update(
                new UpdateDeviceCommand(deviceId, request.status())
        );

        return result.fold(
                success -> ResponseEntity.ok(deviceResourceMapper.toResource(success)),
                error -> {
                    throw toException(error);
                }
        );
    }

    @DeleteMapping("/devices/{deviceId}")
    @Operation(summary = "Delete a device")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Device deleted"),
            @ApiResponse(responseCode = "400", description = "Invalid device id"),
            @ApiResponse(responseCode = "404", description = "Device not found"),
            @ApiResponse(responseCode = "409", description = "Device still has sensor readings")
    })
    public ResponseEntity<Void> delete(@PathVariable("deviceId") Long deviceId) {
        Result<Void, DeviceError> result = deviceCommandService.delete(new DeleteDeviceCommand(deviceId));

        return result.fold(
                success -> ResponseEntity.status(HttpStatus.NO_CONTENT).build(),
                error -> {
                    throw toException(error);
                }
        );
    }

    private RuntimeException toException(DeviceError error) {
        return switch (error.messageKey()) {
            case "error.device.not-found" -> new NotFoundException(error.messageKey());
            case "error.device.mac-address.already-exists", "error.device.in-use" ->
                    new ConflictException(error.messageKey());
            default -> new IllegalArgumentException(error.messageKey());
        };
    }
}
