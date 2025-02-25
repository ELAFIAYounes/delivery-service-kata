package com.delivery.rest.api.timeslot;

import com.delivery.domain.entity.DeliveryMode;
import com.delivery.domain.model.TimeSlotModel;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Time Slots", description = "Time Slot management APIs")
@RequestMapping("/api/timeslots")
public interface TimeSlotAPI {

    @Operation(
        summary = "Get available delivery modes",
        description = "Retrieves all available delivery modes (DRIVE, DELIVERY, DELIVERY_TODAY, DELIVERY_ASAP)"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "List of available delivery modes",
            content = @Content(schema = @Schema(implementation = DeliveryMode.class))
        )
    })
    @GetMapping("/delivery-modes")
    ResponseEntity<CollectionModel<DeliveryMode>> getDeliveryModes();

    @Operation(
        summary = "Get available time slots",
        description = "Retrieves all available time slots for a specific delivery mode"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "List of available time slots",
            content = @Content(schema = @Schema(implementation = TimeSlotModel.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid delivery mode provided",
            content = @Content
        )
    })
    @GetMapping
    ResponseEntity<CollectionModel<TimeSlotModel>> getAvailableTimeSlots(
        @Parameter(description = "Delivery mode to filter time slots", required = true)
        @RequestParam DeliveryMode deliveryMode
    );

    @Operation(
        summary = "Reserve a time slot",
        description = "Reserves a specific time slot by its ID"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Time slot successfully reserved",
            content = @Content(schema = @Schema(implementation = TimeSlotModel.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Time slot not found",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Time slot is not available",
            content = @Content
        )
    })
    @PostMapping("/{timeSlotId}/reserve")
    ResponseEntity<TimeSlotModel> reserveTimeSlot(
        @Parameter(description = "ID of the time slot to reserve", required = true)
        @PathVariable Long timeSlotId
    );
}
