package tn.epaviste.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tn.epaviste.dto.request.SellerProfileRequest;
import tn.epaviste.dto.response.*;
import tn.epaviste.service.RFQService;
import tn.epaviste.service.SellerService;

@Tag(name = "Seller", description = "Seller dashboard endpoints")
@RestController
@RequestMapping("/api/seller")
@RequiredArgsConstructor
public class SellerController {

    private final SellerService sellerService;
    private final RFQService rfqService;

    @Operation(summary = "Get seller dashboard statistics")
    @GetMapping("/dashboard")
    public ResponseEntity<SellerStatsResponse> getDashboardStats(Authentication authentication) {
        return ResponseEntity.ok(sellerService.getStats(authentication.getName()));
    }

    @Operation(summary = "Get seller earnings and payment history")
    @GetMapping("/earnings")
    public ResponseEntity<SellerEarningsResponse> getEarnings(Authentication authentication) {
        return ResponseEntity.ok(sellerService.getEarnings(authentication.getName()));
    }

    @Operation(summary = "Get seller profile")
    @GetMapping("/profile")
    public ResponseEntity<SellerProfileResponse> getProfile(Authentication authentication) {
        return ResponseEntity.ok(sellerService.getProfile(authentication.getName()));
    }

    @Operation(summary = "Update seller profile")
    @PutMapping("/profile")
    public ResponseEntity<SellerProfileResponse> updateProfile(
            @RequestBody SellerProfileRequest request,
            Authentication authentication) {
        return ResponseEntity.ok(sellerService.updateProfile(authentication.getName(), request));
    }

    @Operation(summary = "Get seller public profile (no authentication required)")
    @GetMapping("/{sellerId}/public-profile")
    public ResponseEntity<SellerPublicProfileResponse> getPublicProfile(@PathVariable Long sellerId) {
        return ResponseEntity.ok(sellerService.getPublicProfile(sellerId));
    }

    @Operation(summary = "Get open RFQs for sellers with optional filters")
    @GetMapping("/rfqs")
    public ResponseEntity<Page<RFQResponse>> getSellerRFQs(
            @RequestParam(required = false) String carBrand,
            @RequestParam(required = false) String carModel,
            @RequestParam(required = false) String partCategory,
            @RequestParam(required = false) String location,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(rfqService.listOpenRFQsWithFilters(carBrand, carModel, partCategory, location, pageable));
    }
}
