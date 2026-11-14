package greendream.cultivatech.platform.monitoring.interfaces.rest.controllers;

import greendream.cultivatech.platform.monitoring.application.commands.DeleteSensorReadingCommand;
import greendream.cultivatech.platform.monitoring.application.commands.RegisterSensorReadingCommand;
import greendream.cultivatech.platform.monitoring.application.commandservices.SensorReadingCommandService;
import greendream.cultivatech.platform.monitoring.application.queries.GetAllSensorReadingsQuery;
import greendream.cultivatech.platform.monitoring.application.queries.GetSensorReadingsByDeviceIdQuery;
import greendream.cultivatech.platform.monitoring.application.querieservices.SensorReadingQueryService;
import greendream.cultivatech.platform.monitoring.application.results.SensorReadingError;
import greendream.cultivatech.platform.monitoring.application.results.SensorReadingResult;
import greendream.cultivatech.platform.monitoring.interfaces.rest.resources.RegisterSensorReadingRequest;
import greendream.cultivatech.platform.monitoring.interfaces.rest.resources.SensorReadingResource;
import greendream.cultivatech.platform.monitoring.interfaces.rest.transform.SensorReadingResourceMapper;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for sensor reading use cases.
 */
@RestController
@RequestMapping("api/v1")
@Tag(name = "Sensor Readings")
public class SensorReadingController {

    private final SensorReadingCommandService sensorReadingCommandService;
    private final SensorReadingQueryService sensorReadingQueryService;
    private final SensorReadingResourceMapper sensorReadingResourceMapper;

    public SensorReadingController(SensorReadingCommandService sensorReadingCommandService,
                                   SensorReadingQueryService sensorReadingQueryService,
                                   SensorReadingResourceMapper sensorReadingResourceMapper) {
        this.sensorReadingCommandService = sensorReadingCommandService;
        this.sensorReadingQueryService = sensorReadingQueryService;
        this.sensorReadingResourceMapper = sensorReadingResourceMapper;
    }

    @GetMapping(value = "/sensor-readings", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "List all sensor readings")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Readings found")
    })
    public ResponseEntity<List<SensorReadingResource>> getAll() {
        Result<List<SensorReadingResult>, SensorReadingError> result =
                sensorReadingQueryService.getAllSensorReadings(new GetAllSensorReadingsQuery());

        return result.fold(
                success -> ResponseEntity.ok(sensorReadingResourceMapper.toResources(success)),
                error -> {
                    throw toException(error);
                }
        );
    }

    @GetMapping(value = "/devices/{deviceId}/sensor-readings", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "List the readings recorded by a device")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Readings found"),
            @ApiResponse(responseCode = "400", description = "Invalid device id")
    })
    public ResponseEntity<List<SensorReadingResource>> getByDeviceId(@PathVariable("deviceId") Long deviceId) {
        Result<List<SensorReadingResult>, SensorReadingError> result =
                sensorReadingQueryService.getByDeviceId(new GetSensorReadingsByDeviceIdQuery(deviceId));

        return result.fold(
                success -> ResponseEntity.ok(sensorReadingResourceMapper.toResources(success)),
                error -> {
                    throw toException(error);
                }
        );
    }

    @PostMapping(value = "/devices/{deviceId}/sensor-readings",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Register a reading produced by a device")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Reading registered"),
            @ApiResponse(responseCode = "400", description = "Validation or domain error"),
            @ApiResponse(responseCode = "404", description = "Device not found")
    })
    public ResponseEntity<SensorReadingResource> register(
            @PathVariable("deviceId") Long deviceId,
            @Valid @RequestBody RegisterSensorReadingRequest request) {

        Result<SensorReadingResult, SensorReadingError> result = sensorReadingCommandService.register(
                new RegisterSensorReadingCommand(
                        deviceId,
                        request.parameter(),
                        request.value(),
                        request.unit(),
                        request.recordedAt()
                )
        );

        return result.fold(
                success -> ResponseEntity.status(HttpStatus.CREATED)
                        .body(sensorReadingResourceMapper.toResource(success)),
                error -> {
                    throw toException(error);
                }
        );
    }

    @DeleteMapping("/sensor-readings/{sensorReadingId}")
    @Operation(summary = "Delete a sensor reading")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Reading deleted"),
            @ApiResponse(responseCode = "400", description = "Invalid reading id"),
            @ApiResponse(responseCode = "404", description = "Reading not found")
    })
    public ResponseEntity<Void> delete(@PathVariable("sensorReadingId") Long sensorReadingId) {
        Result<Void, SensorReadingError> result =
                sensorReadingCommandService.delete(new DeleteSensorReadingCommand(sensorReadingId));

        return result.fold(
                success -> ResponseEntity.status(HttpStatus.NO_CONTENT).build(),
                error -> {
                    throw toException(error);
                }
        );
    }

    private RuntimeException toException(SensorReadingError error) {
        return switch (error.messageKey()) {
            case "error.sensor.not-found", "error.device.not-found" -> new NotFoundException(error.messageKey());
            default -> new IllegalArgumentException(error.messageKey());
        };
    }
}
