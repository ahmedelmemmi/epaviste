package tn.epaviste.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tn.epaviste.enums.PaymentStatus;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {
    private Long id;
    private Long orderId;
    private PaymentStatus paymentStatus;
    private String paymentMethod;
    private Boolean escrowReleased;
    private LocalDateTime createdAt;
}
