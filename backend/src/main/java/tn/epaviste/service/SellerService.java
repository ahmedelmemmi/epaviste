package tn.epaviste.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.epaviste.dto.request.SellerProfileRequest;
import tn.epaviste.dto.response.*;
import tn.epaviste.entity.*;
import tn.epaviste.enums.*;
import tn.epaviste.exception.ResourceNotFoundException;
import tn.epaviste.exception.UnauthorizedException;
import tn.epaviste.repository.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SellerService {

    private final UserRepository userRepository;
    private final SellerProfileRepository sellerProfileRepository;
    private final QuoteRepository quoteRepository;
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final NotificationRepository notificationRepository;
    private final RFQRepository rfqRepository;
    private final NotificationService notificationService;

    public SellerStatsResponse getStats(String email) {
        User seller = getSellerByEmail(email);
        SellerProfile profile = sellerProfileRepository.findByUser(seller).orElse(null);

        long totalRFQsReceived = quoteRepository.countDistinctRfqBySeller(seller);

        long activeQuotes = quoteRepository.countBySellerAndStatus(seller, QuoteStatus.PENDING);

        List<Order> sellerOrders = orderRepository.findBySellerOrderByCreatedAtDesc(seller);
        long ordersInProgress = sellerOrders.stream()
                .filter(o -> o.getOrderStatus() == OrderStatus.PENDING ||
                             o.getOrderStatus() == OrderStatus.CONFIRMED ||
                             o.getOrderStatus() == OrderStatus.SHIPPED)
                .count();

        BigDecimal totalRevenue = sellerOrders.stream()
                .filter(o -> o.getOrderStatus() == OrderStatus.DELIVERED)
                .map(o -> o.getTotalPrice().subtract(o.getCommissionAmount()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Double rating = profile != null ? profile.getRating() : 0.0;

        List<NotificationResponse> recentNotifications = notificationRepository
                .findByUserOrderByCreatedAtDesc(seller, PageRequest.of(0, 5))
                .getContent().stream()
                .map(this::toNotificationResponse)
                .collect(Collectors.toList());

        return SellerStatsResponse.builder()
                .totalRFQsReceived(totalRFQsReceived)
                .activeQuotes(activeQuotes)
                .ordersInProgress(ordersInProgress)
                .totalRevenue(totalRevenue)
                .rating(rating)
                .recentNotifications(recentNotifications)
                .build();
    }

    public SellerEarningsResponse getEarnings(String email) {
        User seller = getSellerByEmail(email);
        List<Order> sellerOrders = orderRepository.findBySellerOrderByCreatedAtDesc(seller);

        BigDecimal totalEarnings = sellerOrders.stream()
                .filter(o -> o.getOrderStatus() == OrderStatus.DELIVERED)
                .map(Order::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalCommission = sellerOrders.stream()
                .filter(o -> o.getOrderStatus() == OrderStatus.DELIVERED)
                .map(Order::getCommissionAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal netEarnings = totalEarnings.subtract(totalCommission);

        BigDecimal pendingPayouts = sellerOrders.stream()
                .filter(o -> o.getOrderStatus() == OrderStatus.SHIPPED)
                .map(o -> o.getTotalPrice().subtract(o.getCommissionAmount()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<PaymentResponse> paymentHistory = paymentRepository
                .findByOrderSellerOrderByCreatedAtDesc(seller).stream()
                .map(this::toPaymentResponse)
                .collect(Collectors.toList());

        return SellerEarningsResponse.builder()
                .totalEarnings(totalEarnings)
                .totalCommission(totalCommission)
                .netEarnings(netEarnings)
                .pendingPayouts(pendingPayouts)
                .paymentHistory(paymentHistory)
                .build();
    }

    public SellerProfileResponse getProfile(String email) {
        User seller = getSellerByEmail(email);
        SellerProfile profile = sellerProfileRepository.findByUser(seller)
                .orElse(SellerProfile.builder().user(seller).build());
        return toProfileResponse(seller, profile);
    }

    @Transactional
    public SellerProfileResponse updateProfile(String email, SellerProfileRequest request) {
        User seller = getSellerByEmail(email);
        SellerProfile profile = sellerProfileRepository.findByUser(seller)
                .orElse(SellerProfile.builder().user(seller).build());

        if (request.getCompanyName() != null) profile.setCompanyName(request.getCompanyName());
        if (request.getAddress() != null) profile.setAddress(request.getAddress());
        if (request.getDescription() != null) profile.setDescription(request.getDescription());
        if (request.getDeliveryZones() != null) profile.setDeliveryZones(request.getDeliveryZones());
        if (request.getShippingMethods() != null) profile.setShippingMethods(request.getShippingMethods());

        if (request.getPhone() != null) {
            seller.setPhone(request.getPhone());
            userRepository.save(seller);
        }

        SellerProfile saved = sellerProfileRepository.save(profile);
        return toProfileResponse(seller, saved);
    }

    private User getSellerByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        if (user.getRole() != UserRole.SELLER) {
            throw new UnauthorizedException("Only sellers can access this resource");
        }
        return user;
    }

    private SellerProfileResponse toProfileResponse(User seller, SellerProfile profile) {
        return SellerProfileResponse.builder()
                .id(profile.getId())
                .userId(seller.getId())
                .name(seller.getName())
                .email(seller.getEmail())
                .phone(seller.getPhone())
                .companyName(profile.getCompanyName())
                .address(profile.getAddress())
                .description(profile.getDescription())
                .rating(profile.getRating())
                .verified(profile.getVerified())
                .deliveryZones(profile.getDeliveryZones())
                .shippingMethods(profile.getShippingMethods())
                .messagingEnabled(profile.getMessagingEnabled())
                .build();
    }

    private NotificationResponse toNotificationResponse(Notification n) {
        return NotificationResponse.builder()
                .id(n.getId())
                .type(n.getType())
                .message(n.getMessage())
                .read(n.getRead())
                .createdAt(n.getCreatedAt())
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
}
