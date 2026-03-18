package tn.epaviste.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.epaviste.dto.response.OrderResponse;
import tn.epaviste.entity.Order;
import tn.epaviste.entity.User;
import tn.epaviste.enums.OrderStatus;
import tn.epaviste.enums.UserRole;
import tn.epaviste.exception.ResourceNotFoundException;
import tn.epaviste.exception.UnauthorizedException;
import tn.epaviste.repository.OrderRepository;
import tn.epaviste.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final PaymentService paymentService;
    private final NotificationService notificationService;

    public List<OrderResponse> getMyOrders(String email) {
        User user = getUserByEmail(email);
        return orderRepository.findByBuyerOrSellerOrderByCreatedAtDesc(user, user).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public OrderResponse getOrderById(Long id, String email) {
        User user = getUserByEmail(email);
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", id));
        if (!order.getBuyer().getId().equals(user.getId()) &&
                !order.getSeller().getId().equals(user.getId()) &&
                user.getRole() != UserRole.ADMIN) {
            throw new UnauthorizedException("You do not have access to this order");
        }
        return toResponse(order);
    }

    @Transactional
    public OrderResponse confirmDelivery(Long id, String email) {
        User buyer = getUserByEmail(email);
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", id));
        if (!order.getBuyer().getId().equals(buyer.getId())) {
            throw new UnauthorizedException("Only the buyer can confirm delivery");
        }
        if (order.getOrderStatus() != OrderStatus.SHIPPED) {
            throw new IllegalStateException("Only shipped orders can be confirmed as delivered");
        }
        order.setOrderStatus(OrderStatus.DELIVERED);
        Order saved = orderRepository.save(order);
        paymentService.releaseEscrow(saved.getId());
        notificationService.createNotification(
                order.getSeller(), "ORDER_DELIVERED",
                "Order #" + order.getId() + " has been confirmed as delivered. Payment will be released."
        );
        return toResponse(saved);
    }

    @Transactional
    public OrderResponse markShipped(Long id, String email) {
        User seller = getUserByEmail(email);
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", id));
        if (!order.getSeller().getId().equals(seller.getId())) {
            throw new UnauthorizedException("Only the seller can mark as shipped");
        }
        if (order.getOrderStatus() != OrderStatus.PENDING && order.getOrderStatus() != OrderStatus.CONFIRMED) {
            throw new IllegalStateException("Only pending or confirmed orders can be marked as shipped");
        }
        order.setOrderStatus(OrderStatus.SHIPPED);
        Order saved = orderRepository.save(order);
        notificationService.createNotification(
                order.getBuyer(), "ORDER_SHIPPED",
                "Your order #" + order.getId() + " has been shipped! Please confirm receipt once you receive it."
        );
        return toResponse(saved);
    }

    @Transactional
    public OrderResponse updateOrderStatus(Long id, OrderStatus status, String email) {
        User seller = getUserByEmail(email);
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", id));
        if (!order.getSeller().getId().equals(seller.getId())) {
            throw new UnauthorizedException("Only the seller can update order status");
        }
        order.setOrderStatus(status);
        Order saved = orderRepository.save(order);
        if (status == OrderStatus.CONFIRMED) {
            notificationService.createNotification(
                    order.getBuyer(), "ORDER_CONFIRMED",
                    "Your order #" + order.getId() + " has been confirmed by the seller and is being prepared for shipment."
            );
        }
        return toResponse(saved);
    }

    @Transactional
    public OrderResponse cancelOrder(Long id, String email) {
        User user = getUserByEmail(email);
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", id));
        boolean isBuyer = order.getBuyer().getId().equals(user.getId());
        boolean isSeller = order.getSeller().getId().equals(user.getId());
        if (!isBuyer && !isSeller) {
            throw new UnauthorizedException("You do not have access to this order");
        }
        if (order.getOrderStatus() != OrderStatus.PENDING) {
            throw new IllegalStateException("Only pending orders can be cancelled");
        }
        order.setOrderStatus(OrderStatus.CANCELLED);
        Order saved = orderRepository.save(order);
        User otherParty = isBuyer ? order.getSeller() : order.getBuyer();
        String cancelledBy = isBuyer ? "buyer" : "seller";
        notificationService.createNotification(
                otherParty, "ORDER_CANCELLED",
                "Order #" + order.getId() + " has been cancelled by the " + cancelledBy + "."
        );
        return toResponse(saved);
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }

    OrderResponse toResponse(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .buyerId(order.getBuyer().getId())
                .buyerName(order.getBuyer().getName())
                .sellerId(order.getSeller().getId())
                .sellerName(order.getSeller().getName())
                .quoteId(order.getQuote().getId())
                .totalPrice(order.getTotalPrice())
                .commissionAmount(order.getCommissionAmount())
                .orderStatus(order.getOrderStatus())
                .createdAt(order.getCreatedAt())
                .build();
    }
}
