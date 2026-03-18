package tn.epaviste.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tn.epaviste.dto.request.QuoteRequest;
import tn.epaviste.dto.request.UpdateQuoteRequest;
import tn.epaviste.dto.response.QuoteResponse;
import tn.epaviste.service.QuoteService;

import java.util.List;

@Tag(name = "Quotes", description = "Sellers submit quotes for RFQs; buyers accept or reject them")
@RestController
@RequestMapping("/api/quotes")
@RequiredArgsConstructor
public class QuoteController {

    private final QuoteService quoteService;

    @Operation(summary = "Submit a quote",
            description = "Sellers submit a price and terms for a buyer's RFQ")
    @ApiResponse(responseCode = "200", description = "Quote submitted successfully",
            content = @Content(schema = @Schema(implementation = QuoteResponse.class)))
    @ApiResponse(responseCode = "400", description = "Validation error")
    @ApiResponse(responseCode = "401", description = "Not authenticated")
    @PostMapping
    public ResponseEntity<QuoteResponse> submitQuote(@Valid @RequestBody QuoteRequest request,
                                                      Authentication authentication) {
        return ResponseEntity.ok(quoteService.submitQuote(request, authentication.getName()));
    }

    @Operation(summary = "Get quotes for an RFQ",
            description = "Returns all quotes submitted for the specified RFQ")
    @ApiResponse(responseCode = "200", description = "List of quotes for the RFQ")
    @ApiResponse(responseCode = "401", description = "Not authenticated")
    @ApiResponse(responseCode = "404", description = "RFQ not found")
    @GetMapping("/rfq/{rfqId}")
    public ResponseEntity<List<QuoteResponse>> getQuotesForRFQ(
            @Parameter(description = "RFQ ID") @PathVariable Long rfqId,
            Authentication authentication) {
        return ResponseEntity.ok(quoteService.getQuotesForRFQ(rfqId, authentication.getName()));
    }

    @Operation(summary = "Get my quotes",
            description = "Returns all quotes submitted by the authenticated seller")
    @ApiResponse(responseCode = "200", description = "List of seller's quotes")
    @ApiResponse(responseCode = "401", description = "Not authenticated")
    @GetMapping("/my")
    public ResponseEntity<List<QuoteResponse>> getMyQuotes(Authentication authentication) {
        return ResponseEntity.ok(quoteService.getMyQuotes(authentication.getName()));
    }

    @Operation(summary = "Update a quote", description = "Seller updates a pending quote")
    @PutMapping("/{id}")
    public ResponseEntity<QuoteResponse> updateQuote(
            @PathVariable Long id,
            @Valid @RequestBody UpdateQuoteRequest request,
            Authentication authentication) {
        return ResponseEntity.ok(quoteService.updateQuote(id, request, authentication.getName()));
    }

    @Operation(summary = "Withdraw a quote", description = "Seller withdraws a pending quote")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> withdrawQuote(
            @PathVariable Long id,
            Authentication authentication) {
        quoteService.withdrawQuote(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Accept a quote",
            description = "Buyer accepts a quote, which creates an order automatically")
    @ApiResponse(responseCode = "200", description = "Quote accepted and order created",
            content = @Content(schema = @Schema(implementation = QuoteResponse.class)))
    @ApiResponse(responseCode = "401", description = "Not authenticated")
    @ApiResponse(responseCode = "403", description = "Not authorized to accept this quote")
    @ApiResponse(responseCode = "404", description = "Quote not found")
    @PutMapping("/{id}/accept")
    public ResponseEntity<QuoteResponse> acceptQuote(
            @Parameter(description = "Quote ID") @PathVariable Long id,
            Authentication authentication) {
        return ResponseEntity.ok(quoteService.acceptQuote(id, authentication.getName()));
    }

    @Operation(summary = "Reject a quote",
            description = "Buyer rejects a quote")
    @ApiResponse(responseCode = "200", description = "Quote rejected",
            content = @Content(schema = @Schema(implementation = QuoteResponse.class)))
    @ApiResponse(responseCode = "401", description = "Not authenticated")
    @ApiResponse(responseCode = "403", description = "Not authorized to reject this quote")
    @ApiResponse(responseCode = "404", description = "Quote not found")
    @PutMapping("/{id}/reject")
    public ResponseEntity<QuoteResponse> rejectQuote(
            @Parameter(description = "Quote ID") @PathVariable Long id,
            Authentication authentication) {
        return ResponseEntity.ok(quoteService.rejectQuote(id, authentication.getName()));
    }
}
