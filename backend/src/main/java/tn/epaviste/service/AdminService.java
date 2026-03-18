package tn.epaviste.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.epaviste.dto.request.BroadcastNotificationRequest;
import tn.epaviste.dto.request.ResolveDisputeRequest;
import tn.epaviste.dto.request.UpdateUserStatusRequest;
import tn.epaviste.dto.response.*;
import tn.epaviste.entity.*;
import tn.epaviste.enums.*;
import tn.epaviste.exception.ResourceNotFoundException;
import tn.epaviste.repository.*;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final SellerProfileRepository sellerProfileRepository;
    private final RFQRepository rfqRepository;
    private final QuoteRepository quoteRepository;
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final ReviewRepository reviewRepository;
    private final DisputeRepository disputeRepository;
    private final NotificationService notificationService;
    private final PasswordEncoder passwordEncoder;

    private static final String PASSWORD_CHARS =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*";
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private String generateSecurePassword() {
        StringBuilder sb = new StringBuilder(12);
        for (int i = 0; i < 12; i++) {
            sb.append(PASSWORD_CHARS.charAt(SECURE_RANDOM.nextInt(PASSWORD_CHARS.length())));
        }
        return sb.toString();
    }

    // ── Analytics ──────────────────────────────────────────────────────────────

    public AdminStatsResponse getPlatformStats() {
        long totalBuyers = userRepository.countByRole(UserRole.BUYER);
        long totalSellers = userRepository.countByRole(UserRole.SELLER);
        long totalUsers = userRepository.count();
        long activeSellers = sellerProfileRepository.countByVerifiedTrue();
        long totalRFQs = rfqRepository.count();
        long totalQuotes = quoteRepository.count();
        long totalOrdersCompleted = orderRepository.countByOrderStatus(OrderStatus.DELIVERED);
        BigDecimal totalGMV = orderRepository.sumTotalGMV();
        BigDecimal totalCommission = orderRepository.sumTotalCommission();
        double conversionRate = totalRFQs == 0 ? 0.0 :
                (double) totalOrdersCompleted / totalRFQs * 100;

        return AdminStatsResponse.builder()
                .totalUsers(totalUsers)
                .totalBuyers(totalBuyers)
                .totalSellers(totalSellers)
                .activeSellers(activeSellers)
                .totalRFQs(totalRFQs)
                .totalQuotes(totalQuotes)
                .totalOrdersCompleted(totalOrdersCompleted)
                .totalGMV(totalGMV)
                .totalCommissionRevenue(totalCommission)
                .rfqToOrderConversionRate(conversionRate)
                .build();
    }

    // ── User Management ────────────────────────────────────────────────────────

    public Page<AdminUserResponse> getUsers(UserRole role, String search, Pageable pageable) {
        return userRepository.findByRoleAndSearch(role, search, pageable)
                .map(this::toAdminUserResponse);
    }

    public AdminUserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
        return toAdminUserResponse(user);
    }

    @Transactional
    public AdminUserResponse updateUserStatus(Long id, UpdateUserStatusRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
        user.setStatus(request.getStatus());
        return toAdminUserResponse(userRepository.save(user));
    }

    @Transactional
    public void deactivateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
        user.setStatus(UserStatus.DEACTIVATED);
        userRepository.save(user);
    }

    @Transactional
    public AdminUserResponse resetUserPassword(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
        String tempPassword = generateSecurePassword();
        user.setPassword(passwordEncoder.encode(tempPassword));
        userRepository.save(user);
        notificationService.createNotification(user, "PASSWORD_RESET",
                "Your password has been reset by an administrator. Temporary password: " + tempPassword);
        return toAdminUserResponse(user);
    }

    // ── Seller Verification ────────────────────────────────────────────────────

    public Page<SellerProfileResponse> getPendingSellerVerifications(Pageable pageable) {
        return sellerProfileRepository.findByVerifiedFalse(pageable)
                .map(this::toSellerProfileResponse);
    }

    @Transactional
    public SellerProfileResponse approveSeller(Long userId) {
        User seller = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));
        SellerProfile profile = sellerProfileRepository.findByUser(seller)
                .orElseThrow(() -> new ResourceNotFoundException("SellerProfile for user", userId));
        profile.setVerified(true);
        SellerProfile saved = sellerProfileRepository.save(profile);
        notificationService.createNotification(seller, "SELLER_APPROVED",
                "Your seller account has been verified. You can now receive orders.");
        return toSellerProfileResponse(saved);
    }

    @Transactional
    public SellerProfileResponse rejectSeller(Long userId) {
        User seller = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));
        SellerProfile profile = sellerProfileRepository.findByUser(seller)
                .orElseThrow(() -> new ResourceNotFoundException("SellerProfile for user", userId));
        profile.setVerified(false);
        SellerProfile saved = sellerProfileRepository.save(profile);
        notificationService.createNotification(seller, "SELLER_REJECTED",
                "Your seller verification request has been rejected. Please contact support for more information.");
        return toSellerProfileResponse(saved);
    }

    // ── RFQ Monitoring ─────────────────────────────────────────────────────────

    public Page<RFQResponse> getAllRFQs(Pageable pageable) {
        return rfqRepository.findAll(pageable).map(this::toRFQResponse);
    }

    @Transactional
    public RFQResponse closeRFQ(Long id) {
        RFQ rfq = rfqRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RFQ", id));
        rfq.setStatus(RFQStatus.CLOSED);
        return toRFQResponse(rfqRepository.save(rfq));
    }

    @Transactional
    public void deleteRFQ(Long id) {
        if (!rfqRepository.existsById(id)) {
            throw new ResourceNotFoundException("RFQ", id);
        }
        rfqRepository.deleteById(id);
    }

    // ── Quote Monitoring ───────────────────────────────────────────────────────

    public Page<QuoteResponse> getAllQuotes(Pageable pageable) {
        return quoteRepository.findAll(pageable).map(this::toQuoteResponse);
    }

    // ── Order Management ───────────────────────────────────────────────────────

    public Page<OrderResponse> getAllOrders(Pageable pageable) {
        return orderRepository.findAll(pageable).map(this::toOrderResponse);
    }

    @Transactional
    public OrderResponse updateOrderStatus(Long id, OrderStatus newStatus) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", id));
        order.setOrderStatus(newStatus);
        return toOrderResponse(orderRepository.save(order));
    }

    @Transactional
    public OrderResponse cancelOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", id));
        order.setOrderStatus(OrderStatus.CANCELLED);
        notificationService.createNotification(order.getBuyer(), "ORDER_CANCELLED",
                "Order #" + id + " has been cancelled by an administrator.");
        notificationService.createNotification(order.getSeller(), "ORDER_CANCELLED",
                "Order #" + id + " has been cancelled by an administrator.");
        return toOrderResponse(orderRepository.save(order));
    }

    // ── Payment & Commission ───────────────────────────────────────────────────

    public AdminStatsResponse getPlatformRevenue() {
        return getPlatformStats();
    }

    public Page<PaymentResponse> getTransactionHistory(Pageable pageable) {
        return paymentRepository.findAll(pageable).map(this::toPaymentResponse);
    }

    public Page<PaymentResponse> getPayoutReports(Pageable pageable) {
        return paymentRepository.findAll(pageable).map(this::toPaymentResponse);
    }

    // ── Dispute Resolution ─────────────────────────────────────────────────────

    public Page<AdminDisputeResponse> getDisputes(Pageable pageable) {
        return disputeRepository.findAll(pageable).map(this::toDisputeResponse);
    }

    @Transactional
    public AdminDisputeResponse resolveDispute(Long id, ResolveDisputeRequest request) {
        Dispute dispute = disputeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dispute", id));
        dispute.setStatus(DisputeStatus.RESOLVED);
        dispute.setResolution(request.getResolution());
        dispute.setResolvedAt(LocalDateTime.now());
        notificationService.createNotification(dispute.getComplainant(), "DISPUTE_RESOLVED",
                "Your dispute #" + id + " has been resolved.");
        return toDisputeResponse(disputeRepository.save(dispute));
    }

    // ── Reviews Monitoring ─────────────────────────────────────────────────────

    public Page<ReviewResponse> getAllReviews(Pageable pageable) {
        return reviewRepository.findAllByOrderByCreatedAtDesc(pageable).map(this::toReviewResponse);
    }

    @Transactional
    public void deleteReview(Long id) {
        if (!reviewRepository.existsById(id)) {
            throw new ResourceNotFoundException("Review", id);
        }
        reviewRepository.deleteById(id);
    }

    // ── Notifications & Announcements ──────────────────────────────────────────

    @Transactional
    public void broadcastNotification(BroadcastNotificationRequest request) {
        List<User> targets;
        if (request.getTargetRole() != null && !request.getTargetRole().isBlank()) {
            UserRole role = UserRole.valueOf(request.getTargetRole().toUpperCase());
            targets = userRepository.findByRole(role);
        } else {
            targets = userRepository.findAll();
        }
        targets.forEach(user -> notificationService.createNotification(
                user, request.getType(), request.getMessage()));
    }

    // ── Mapping helpers ────────────────────────────────────────────────────────

    private AdminUserResponse toAdminUserResponse(User u) {
        return AdminUserResponse.builder()
                .id(u.getId())
                .name(u.getName())
                .email(u.getEmail())
                .phone(u.getPhone())
                .role(u.getRole())
                .status(u.getStatus())
                .createdAt(u.getCreatedAt())
                .build();
    }

    private SellerProfileResponse toSellerProfileResponse(SellerProfile p) {
        User seller = p.getUser();
        return SellerProfileResponse.builder()
                .id(p.getId())
                .userId(seller.getId())
                .name(seller.getName())
                .email(seller.getEmail())
                .phone(seller.getPhone())
                .companyName(p.getCompanyName())
                .address(p.getAddress())
                .description(p.getDescription())
                .rating(p.getRating())
                .verified(p.getVerified())
                .deliveryZones(p.getDeliveryZones())
                .shippingMethods(p.getShippingMethods())
                .messagingEnabled(p.getMessagingEnabled())
                .build();
    }

    private RFQResponse toRFQResponse(RFQ rfq) {
        List<String> images = rfq.getImages().stream()
                .map(img -> img.getImageUrl())
                .collect(Collectors.toList());
        return RFQResponse.builder()
                .id(rfq.getId())
                .buyerId(rfq.getBuyer().getId())
                .buyerName(rfq.getBuyer().getName())
                .carBrand(rfq.getCarBrand())
                .carModel(rfq.getCarModel())
                .carYear(rfq.getCarYear())
                .vin(rfq.getVin())
                .partName(rfq.getPartName())
                .partCategory(rfq.getPartCategory())
                .preferredCondition(rfq.getPreferredCondition())
                .description(rfq.getDescription())
                .location(rfq.getLocation())
                .status(rfq.getStatus())
                .createdAt(rfq.getCreatedAt())
                .images(images)
                .quoteCount(quoteRepository.countByRfq(rfq))
                .build();
    }

    private QuoteResponse toQuoteResponse(Quote q) {
        SellerProfile profile = sellerProfileRepository.findByUser(q.getSeller()).orElse(null);
        return QuoteResponse.builder()
                .id(q.getId())
                .rfqId(q.getRfq().getId())
                .sellerId(q.getSeller().getId())
                .sellerName(q.getSeller().getName())
                .sellerCompany(profile != null ? profile.getCompanyName() : null)
                .price(q.getPrice())
                .condition(q.getCondition())
                .deliveryTime(q.getDeliveryTime())
                .shippingMethod(q.getShippingMethod())
                .message(q.getMessage())
                .status(q.getStatus())
                .createdAt(q.getCreatedAt())
                .build();
    }

    private OrderResponse toOrderResponse(Order o) {
        return OrderResponse.builder()
                .id(o.getId())
                .buyerId(o.getBuyer().getId())
                .buyerName(o.getBuyer().getName())
                .sellerId(o.getSeller().getId())
                .sellerName(o.getSeller().getName())
                .quoteId(o.getQuote().getId())
                .totalPrice(o.getTotalPrice())
                .commissionAmount(o.getCommissionAmount())
                .orderStatus(o.getOrderStatus())
                .createdAt(o.getCreatedAt())
                .build();
    }

    private PaymentResponse toPaymentResponse(Payment p) {
        return PaymentResponse.builder()
                .id(p.getId())
                .orderId(p.getOrder().getId())
                .paymentStatus(p.getPaymentStatus())
                .paymentMethod(p.getPaymentMethod())
                .escrowReleased(p.getEscrowReleased())
                .createdAt(p.getCreatedAt())
                .build();
    }

    private AdminDisputeResponse toDisputeResponse(Dispute d) {
        return AdminDisputeResponse.builder()
                .id(d.getId())
                .orderId(d.getOrder().getId())
                .complainantId(d.getComplainant().getId())
                .complainantName(d.getComplainant().getName())
                .reason(d.getReason())
                .status(d.getStatus())
                .resolution(d.getResolution())
                .createdAt(d.getCreatedAt())
                .resolvedAt(d.getResolvedAt())
                .build();
    }

    private ReviewResponse toReviewResponse(Review r) {
        return ReviewResponse.builder()
                .id(r.getId())
                .orderId(r.getOrder().getId())
                .reviewerId(r.getReviewer().getId())
                .reviewerName(r.getReviewer().getName())
                .rating(r.getRating())
                .comment(r.getComment())
                .createdAt(r.getCreatedAt())
                .build();
    }
}
