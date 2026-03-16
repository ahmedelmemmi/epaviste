package tn.epaviste.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tn.epaviste.dto.request.RFQRequest;
import tn.epaviste.dto.response.RFQResponse;
import tn.epaviste.service.RFQService;

import java.util.List;

@Tag(name = "RFQs", description = "Manage Requests For Quotes — buyers post RFQs, sellers submit quotes")
@RestController
@RequestMapping("/api/rfqs")
@RequiredArgsConstructor
public class RFQController {

    private final RFQService rfqService;

    @Operation(summary = "Create a new RFQ",
            description = "Buyers post a request for a car part. Requires BUYER role.")
    @ApiResponse(responseCode = "200", description = "RFQ created successfully",
            content = @Content(schema = @Schema(implementation = RFQResponse.class)))
    @ApiResponse(responseCode = "400", description = "Validation error")
    @ApiResponse(responseCode = "401", description = "Not authenticated")
    @PostMapping
    public ResponseEntity<RFQResponse> createRFQ(@Valid @RequestBody RFQRequest request,
                                                   Authentication authentication) {
        return ResponseEntity.ok(rfqService.createRFQ(request, authentication.getName()));
    }

    @Operation(summary = "List all open RFQs",
            description = "Returns a paginated list of all RFQs with OPEN status")
    @ApiResponse(responseCode = "200", description = "Page of open RFQs")
    @GetMapping
    public ResponseEntity<Page<RFQResponse>> listOpenRFQs(
            @Parameter(description = "Pagination (page, size, sort)") @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(rfqService.listOpenRFQs(pageable));
    }

    @Operation(summary = "Get an RFQ by ID")
    @ApiResponse(responseCode = "200", description = "RFQ details",
            content = @Content(schema = @Schema(implementation = RFQResponse.class)))
    @ApiResponse(responseCode = "404", description = "RFQ not found")
    @GetMapping("/{id}")
    public ResponseEntity<RFQResponse> getRFQ(
            @Parameter(description = "RFQ ID") @PathVariable Long id) {
        return ResponseEntity.ok(rfqService.getRFQById(id));
    }

    @Operation(summary = "Get my RFQs",
            description = "Returns all RFQs created by the authenticated buyer")
    @ApiResponse(responseCode = "200", description = "List of buyer's RFQs")
    @ApiResponse(responseCode = "401", description = "Not authenticated")
    @GetMapping("/my")
    public ResponseEntity<List<RFQResponse>> getMyRFQs(Authentication authentication) {
        return ResponseEntity.ok(rfqService.getMyRFQs(authentication.getName()));
    }

    @Operation(summary = "Cancel an RFQ",
            description = "Cancels an open RFQ. Only the buyer who created it can cancel it.")
    @ApiResponse(responseCode = "204", description = "RFQ cancelled successfully")
    @ApiResponse(responseCode = "401", description = "Not authenticated")
    @ApiResponse(responseCode = "403", description = "Not authorized to cancel this RFQ")
    @ApiResponse(responseCode = "404", description = "RFQ not found")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelRFQ(
            @Parameter(description = "RFQ ID") @PathVariable Long id,
            Authentication authentication) {
        rfqService.cancelRFQ(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}
