package tn.epaviste.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tn.epaviste.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    private Long id;
    private Long buyerId;
    private String buyerName;
    private Long sellerId;
    private String sellerName;
    private Long quoteId;
    private BigDecimal totalPrice;
    private BigDecimal commissionAmount;
    private OrderStatus orderStatus;
    private LocalDateTime createdAt;
}
