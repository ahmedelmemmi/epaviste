package tn.epaviste.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.epaviste.dto.response.PaymentResponse;
import tn.epaviste.entity.Order;
import tn.epaviste.entity.Payment;
import tn.epaviste.enums.PaymentStatus;
import tn.epaviste.exception.ResourceNotFoundException;
import tn.epaviste.repository.OrderRepository;
import tn.epaviste.repository.PaymentRepository;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    @Transactional
    public PaymentResponse processPayment(Long orderId, String paymentMethod) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", orderId));

        Payment payment = paymentRepository.findByOrder(order)
                .orElse(Payment.builder().order(order).build());

        payment.setPaymentMethod(paymentMethod);
        payment.setPaymentStatus(PaymentStatus.COMPLETED);
        payment.setEscrowReleased(false);

        Payment saved = paymentRepository.save(payment);
        return toResponse(saved);
    }

    @Transactional
    public void releaseEscrow(Long orderId) {
        paymentRepository.findByOrderId(orderId).ifPresent(payment -> {
            payment.setEscrowReleased(true);
            paymentRepository.save(payment);
        });
    }

    public PaymentResponse getPaymentByOrderId(Long orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found for order: " + orderId));
        return toResponse(payment);
    }

    PaymentResponse toResponse(Payment payment) {
        return PaymentResponse.builder()
                .id(payment.getId())
                .orderId(payment.getOrder().getId())
                .paymentStatus(payment.getPaymentStatus())
                .paymentMethod(payment.getPaymentMethod())
                .escrowReleased(payment.getEscrowReleased())
                .createdAt(payment.getCreatedAt())
                .build();
    }
}
