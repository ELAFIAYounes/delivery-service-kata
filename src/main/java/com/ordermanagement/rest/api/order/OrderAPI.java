package com.ordermanagement.rest.api.order;

import com.ordermanagement.domain.entity.Order;
import com.ordermanagement.rest.api.error.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@Tag(name = "Order Management", description = "APIs for managing orders and refund requests")
@Validated
public interface OrderAPI {

    @Operation(summary = "Get customer order history", description = "Retrieves the order history for a specific customer")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Orders found successfully",
            content = @Content(schema = @Schema(implementation = Order.class))),
        @ApiResponse(responseCode = "404", description = "Customer not found", 
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/customers/{customerId}/orders")
    ResponseEntity<List<Order>> getCustomerOrderHistory(
        @Parameter(description = "ID of the customer to get orders for", required = true) 
        @PathVariable String customerId
    );

    @Operation(summary = "Submit a refund request", description = "Submit a refund request for a specific order item with evidence")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Refund request submitted successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data", 
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "Order item not found", 
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/orders/items/{orderItemId}/refund")
    ResponseEntity<Void> submitRefundRequest(
        @Parameter(description = "ID of the order item to request refund for", required = true)
        @PathVariable @Min(1) Long orderItemId,
        @Parameter(description = "Refund request details including evidence", required = true) 
        @Valid @RequestBody RefundRequestDTO refundRequest
    );
}
