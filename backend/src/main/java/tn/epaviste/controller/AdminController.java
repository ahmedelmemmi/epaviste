package tn.epaviste.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tn.epaviste.dto.request.BroadcastNotificationRequest;
import tn.epaviste.dto.request.ResolveDisputeRequest;
import tn.epaviste.dto.request.UpdateUserStatusRequest;
import tn.epaviste.dto.response.*;
import tn.epaviste.enums.OrderStatus;
import tn.epaviste.enums.UserRole;
import tn.epaviste.service.AdminService;

@Tag(name = "Admin", description = "Admin dashboard endpoints — requires ADMIN role")
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    // ── Analytics ──────────────────────────────────────────────────────────────

    @Operation(summary = "Get platform-wide statistics")
    @GetMapping("/stats")
    public ResponseEntity<AdminStatsResponse> getPlatformStats() {
        return ResponseEntity.ok(adminService.getPlatformStats());
    }

    // ── User Management ────────────────────────────────────────────────────────

    @Operation(summary = "List all users with optional role and search filters")
    @GetMapping("/users")
    public ResponseEntity<Page<AdminUserResponse>> getUsers(
            @RequestParam(required = false) UserRole role,
            @RequestParam(required = false) String search,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(adminService.getUsers(role, search, pageable));
    }

    @Operation(summary = "Get user details by ID")
    @GetMapping("/users/{id}")
    public ResponseEntity<AdminUserResponse> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.getUserById(id));
    }

    @Operation(summary = "Update user status (ACTIVE / SUSPENDED / DEACTIVATED)")
    @PutMapping("/users/{id}/status")
    public ResponseEntity<AdminUserResponse> updateUserStatus(
            @PathVariable Long id,
            @RequestBody UpdateUserStatusRequest request) {
        return ResponseEntity.ok(adminService.updateUserStatus(id, request));
    }

    @Operation(summary = "Deactivate a user account")
    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deactivateUser(@PathVariable Long id) {
        adminService.deactivateUser(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Reset a user's password and notify them")
    @PostMapping("/users/{id}/reset-password")
    public ResponseEntity<AdminUserResponse> resetUserPassword(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.resetUserPassword(id));
    }

    // ── Seller Verification ────────────────────────────────────────────────────

    @Operation(summary = "List seller verification requests (unverified sellers)")
    @GetMapping("/sellers/pending")
    public ResponseEntity<Page<SellerProfileResponse>> getPendingSellerVerifications(
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(adminService.getPendingSellerVerifications(pageable));
    }

    @Operation(summary = "Approve a seller (grant verification badge)")
    @PutMapping("/sellers/{userId}/approve")
    public ResponseEntity<SellerProfileResponse> approveSeller(@PathVariable Long userId) {
        return ResponseEntity.ok(adminService.approveSeller(userId));
    }

    @Operation(summary = "Reject a seller verification request")
    @PutMapping("/sellers/{userId}/reject")
    public ResponseEntity<SellerProfileResponse> rejectSeller(@PathVariable Long userId) {
        return ResponseEntity.ok(adminService.rejectSeller(userId));
    }

    // ── RFQ Monitoring ─────────────────────────────────────────────────────────

    @Operation(summary = "List all RFQs on the platform")
    @GetMapping("/rfqs")
    public ResponseEntity<Page<RFQResponse>> getAllRFQs(
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(adminService.getAllRFQs(pageable));
    }

    @Operation(summary = "Close an RFQ")
    @PutMapping("/rfqs/{id}/close")
    public ResponseEntity<RFQResponse> closeRFQ(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.closeRFQ(id));
    }

    @Operation(summary = "Delete an RFQ")
    @DeleteMapping("/rfqs/{id}")
    public ResponseEntity<Void> deleteRFQ(@PathVariable Long id) {
        adminService.deleteRFQ(id);
        return ResponseEntity.noContent().build();
    }

    // ── Quote Monitoring ───────────────────────────────────────────────────────

    @Operation(summary = "List all quotes on the platform")
    @GetMapping("/quotes")
    public ResponseEntity<Page<QuoteResponse>> getAllQuotes(
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(adminService.getAllQuotes(pageable));
    }

    // ── Order Management ───────────────────────────────────────────────────────

    @Operation(summary = "List all orders on the platform")
    @GetMapping("/orders")
    public ResponseEntity<Page<OrderResponse>> getAllOrders(
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(adminService.getAllOrders(pageable));
    }

    @Operation(summary = "Update an order's status")
    @PutMapping("/orders/{id}/status")
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @PathVariable Long id,
            @RequestParam OrderStatus status) {
        return ResponseEntity.ok(adminService.updateOrderStatus(id, status));
    }

    @Operation(summary = "Cancel an order")
    @PutMapping("/orders/{id}/cancel")
    public ResponseEntity<OrderResponse> cancelOrder(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.cancelOrder(id));
    }

    // ── Payment & Commission ───────────────────────────────────────────────────

    @Operation(summary = "Get platform revenue summary")
    @GetMapping("/revenue")
    public ResponseEntity<AdminStatsResponse> getPlatformRevenue() {
        return ResponseEntity.ok(adminService.getPlatformRevenue());
    }

    @Operation(summary = "Get all transaction (payment) history")
    @GetMapping("/transactions")
    public ResponseEntity<Page<PaymentResponse>> getTransactionHistory(
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(adminService.getTransactionHistory(pageable));
    }

    @Operation(summary = "Get seller payout reports")
    @GetMapping("/payouts")
    public ResponseEntity<Page<PaymentResponse>> getPayoutReports(
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(adminService.getPayoutReports(pageable));
    }

    // ── Dispute Resolution ─────────────────────────────────────────────────────

    @Operation(summary = "List all disputes")
    @GetMapping("/disputes")
    public ResponseEntity<Page<AdminDisputeResponse>> getDisputes(
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(adminService.getDisputes(pageable));
    }

    @Operation(summary = "Resolve a dispute")
    @PutMapping("/disputes/{id}/resolve")
    public ResponseEntity<AdminDisputeResponse> resolveDispute(
            @PathVariable Long id,
            @RequestBody ResolveDisputeRequest request) {
        return ResponseEntity.ok(adminService.resolveDispute(id, request));
    }

    // ── Reviews Monitoring ─────────────────────────────────────────────────────

    @Operation(summary = "List all reviews on the platform")
    @GetMapping("/reviews")
    public ResponseEntity<Page<ReviewResponse>> getAllReviews(
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(adminService.getAllReviews(pageable));
    }

    @Operation(summary = "Delete a review")
    @DeleteMapping("/reviews/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long id) {
        adminService.deleteReview(id);
        return ResponseEntity.noContent().build();
    }

    // ── Notifications & Announcements ──────────────────────────────────────────

    @Operation(summary = "Send a platform-wide or targeted notification")
    @PostMapping("/notifications")
    public ResponseEntity<Void> broadcastNotification(
            @RequestBody BroadcastNotificationRequest request) {
        adminService.broadcastNotification(request);
        return ResponseEntity.noContent().build();
    }
}
