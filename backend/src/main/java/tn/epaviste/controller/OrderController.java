package tn.epaviste.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tn.epaviste.dto.response.OrderResponse;
import tn.epaviste.service.OrderService;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/my")
    public ResponseEntity<List<OrderResponse>> getMyOrders(Authentication authentication) {
        return ResponseEntity.ok(orderService.getMyOrders(authentication.getName()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable Long id,
                                                   Authentication authentication) {
        return ResponseEntity.ok(orderService.getOrderById(id, authentication.getName()));
    }

    @PutMapping("/{id}/confirm-delivery")
    public ResponseEntity<OrderResponse> confirmDelivery(@PathVariable Long id,
                                                          Authentication authentication) {
        return ResponseEntity.ok(orderService.confirmDelivery(id, authentication.getName()));
    }

    @PutMapping("/{id}/ship")
    public ResponseEntity<OrderResponse> markShipped(@PathVariable Long id,
                                                      Authentication authentication) {
        return ResponseEntity.ok(orderService.markShipped(id, authentication.getName()));
    }
}
