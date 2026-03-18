package tn.epaviste.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.epaviste.dto.request.BuyerProfileRequest;
import tn.epaviste.dto.request.VehicleRequest;
import tn.epaviste.dto.response.BuyerStatsResponse;
import tn.epaviste.dto.response.NotificationResponse;
import tn.epaviste.dto.response.VehicleResponse;
import tn.epaviste.entity.*;
import tn.epaviste.enums.OrderStatus;
import tn.epaviste.enums.UserRole;
import tn.epaviste.exception.ResourceNotFoundException;
import tn.epaviste.exception.UnauthorizedException;
import tn.epaviste.repository.*;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BuyerService {

    private final UserRepository userRepository;
    private final RFQRepository rfqRepository;
    private final QuoteRepository quoteRepository;
    private final OrderRepository orderRepository;
    private final NotificationRepository notificationRepository;
    private final VehicleRepository vehicleRepository;

    public BuyerStatsResponse getStats(String email) {
        User buyer = getBuyerByEmail(email);

        long totalRFQsSubmitted = rfqRepository.findByBuyer(buyer).size();

        long quotesReceived = rfqRepository.findByBuyer(buyer).stream()
                .mapToLong(rfq -> quoteRepository.countByRfq(rfq))
                .sum();

        List<Order> buyerOrders = orderRepository.findByBuyerOrderByCreatedAtDesc(buyer);

        long activeOrders = buyerOrders.stream()
                .filter(o -> o.getOrderStatus() == OrderStatus.PENDING
                        || o.getOrderStatus() == OrderStatus.CONFIRMED
                        || o.getOrderStatus() == OrderStatus.SHIPPED)
                .count();

        long completedOrders = buyerOrders.stream()
                .filter(o -> o.getOrderStatus() == OrderStatus.DELIVERED)
                .count();

        List<NotificationResponse> recentNotifications = notificationRepository
                .findByUserOrderByCreatedAtDesc(buyer, PageRequest.of(0, 5))
                .getContent().stream()
                .map(this::toNotificationResponse)
                .collect(Collectors.toList());

        return BuyerStatsResponse.builder()
                .totalRFQsSubmitted(totalRFQsSubmitted)
                .quotesReceived(quotesReceived)
                .activeOrders(activeOrders)
                .completedOrders(completedOrders)
                .recentNotifications(recentNotifications)
                .build();
    }

    public User getProfile(String email) {
        return getBuyerByEmail(email);
    }

    @Transactional
    public User updateProfile(String email, BuyerProfileRequest request) {
        User buyer = getBuyerByEmail(email);
        if (request.getName() != null && !request.getName().isBlank()) {
            buyer.setName(request.getName());
        }
        if (request.getPhone() != null) {
            buyer.setPhone(request.getPhone());
        }
        return userRepository.save(buyer);
    }

    public List<VehicleResponse> getVehicles(String email) {
        User buyer = getBuyerByEmail(email);
        return vehicleRepository.findByBuyerOrderByCreatedAtDesc(buyer).stream()
                .map(this::toVehicleResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public VehicleResponse addVehicle(String email, VehicleRequest request) {
        User buyer = getBuyerByEmail(email);
        Vehicle vehicle = Vehicle.builder()
                .buyer(buyer)
                .brand(request.getBrand())
                .model(request.getModel())
                .year(request.getYear())
                .vin(request.getVin())
                .build();
        return toVehicleResponse(vehicleRepository.save(vehicle));
    }

    @Transactional
    public void deleteVehicle(String email, Long vehicleId) {
        User buyer = getBuyerByEmail(email);
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle", vehicleId));
        if (!vehicle.getBuyer().getId().equals(buyer.getId())) {
            throw new UnauthorizedException("You can only delete your own vehicles");
        }
        vehicleRepository.delete(vehicle);
    }

    private User getBuyerByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        if (user.getRole() != UserRole.BUYER) {
            throw new UnauthorizedException("Only buyers can access this resource");
        }
        return user;
    }

    private VehicleResponse toVehicleResponse(Vehicle v) {
        return VehicleResponse.builder()
                .id(v.getId())
                .brand(v.getBrand())
                .model(v.getModel())
                .year(v.getYear())
                .vin(v.getVin())
                .createdAt(v.getCreatedAt())
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
}
