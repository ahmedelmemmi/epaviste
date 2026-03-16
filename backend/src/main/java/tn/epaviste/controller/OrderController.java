package tn.epaviste.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tn.epaviste.dto.response.OrderResponse;
import tn.epaviste.service.OrderService;

import java.util.List;

@Tag(name = "Orders", description = "Track and manage orders created when a quote is accepted")
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @Operation(summary = "Get my orders",
            description = "Returns all orders for the authenticated user (buyer or seller)")
    @ApiResponse(responseCode = "200", description = "List of orders")
    @ApiResponse(responseCode = "401", description = "Not authenticated")
    @GetMapping("/my")
    public ResponseEntity<List<OrderResponse>> getMyOrders(Authentication authentication) {
        return ResponseEntity.ok(orderService.getMyOrders(authentication.getName()));
    }

    @Operation(summary = "Get an order by ID")
    @ApiResponse(responseCode = "200", description = "Order details",
            content = @Content(schema = @Schema(implementation = OrderResponse.class)))
    @ApiResponse(responseCode = "401", description = "Not authenticated")
    @ApiResponse(responseCode = "403", description = "Not authorized to view this order")
    @ApiResponse(responseCode = "404", description = "Order not found")
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrder(
            @Parameter(description = "Order ID") @PathVariable Long id,
            Authentication authentication) {
        return ResponseEntity.ok(orderService.getOrderById(id, authentication.getName()));
    }

    @Operation(summary = "Confirm delivery",
            description = "Buyer confirms that the part has been received and the order is complete")
    @ApiResponse(responseCode = "200", description = "Delivery confirmed",
            content = @Content(schema = @Schema(implementation = OrderResponse.class)))
    @ApiResponse(responseCode = "401", description = "Not authenticated")
    @ApiResponse(responseCode = "403", description = "Not authorized to confirm delivery for this order")
    @ApiResponse(responseCode = "404", description = "Order not found")
    @PutMapping("/{id}/confirm-delivery")
    public ResponseEntity<OrderResponse> confirmDelivery(
            @Parameter(description = "Order ID") @PathVariable Long id,
            Authentication authentication) {
        return ResponseEntity.ok(orderService.confirmDelivery(id, authentication.getName()));
    }

    @Operation(summary = "Mark order as shipped",
            description = "Seller marks the order as shipped after dispatching the part")
    @ApiResponse(responseCode = "200", description = "Order marked as shipped",
            content = @Content(schema = @Schema(implementation = OrderResponse.class)))
    @ApiResponse(responseCode = "401", description = "Not authenticated")
    @ApiResponse(responseCode = "403", description = "Not authorized to ship this order")
    @ApiResponse(responseCode = "404", description = "Order not found")
    @PutMapping("/{id}/ship")
    public ResponseEntity<OrderResponse> markShipped(
            @Parameter(description = "Order ID") @PathVariable Long id,
            Authentication authentication) {
        return ResponseEntity.ok(orderService.markShipped(id, authentication.getName()));
    }
}
