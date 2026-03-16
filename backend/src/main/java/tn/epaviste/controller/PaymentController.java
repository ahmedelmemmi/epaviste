package tn.epaviste.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.epaviste.dto.response.PaymentResponse;
import tn.epaviste.service.PaymentService;

@Tag(name = "Payments", description = "Process and query payments for orders")
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @Operation(summary = "Process a payment",
            description = "Initiates payment processing for an order")
    @ApiResponse(responseCode = "200", description = "Payment processed",
            content = @Content(schema = @Schema(implementation = PaymentResponse.class)))
    @ApiResponse(responseCode = "400", description = "Validation error")
    @ApiResponse(responseCode = "401", description = "Not authenticated")
    @ApiResponse(responseCode = "404", description = "Order not found")
    @PostMapping("/process")
    public ResponseEntity<PaymentResponse> processPayment(@Valid @RequestBody PaymentRequest request) {
        return ResponseEntity.ok(paymentService.processPayment(request.getOrderId(), request.getPaymentMethod()));
    }

    @Operation(summary = "Get payment status by order",
            description = "Returns payment details for the specified order")
    @ApiResponse(responseCode = "200", description = "Payment details",
            content = @Content(schema = @Schema(implementation = PaymentResponse.class)))
    @ApiResponse(responseCode = "401", description = "Not authenticated")
    @ApiResponse(responseCode = "404", description = "Payment or order not found")
    @GetMapping("/order/{orderId}")
    public ResponseEntity<PaymentResponse> getPaymentStatus(
            @Parameter(description = "Order ID") @PathVariable Long orderId) {
        return ResponseEntity.ok(paymentService.getPaymentByOrderId(orderId));
    }

    @Data
    static class PaymentRequest {
        @NotNull(message = "Order ID is required")
        private Long orderId;
        @NotBlank(message = "Payment method is required")
        private String paymentMethod;
    }
}
