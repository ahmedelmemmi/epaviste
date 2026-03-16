package tn.epaviste.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tn.epaviste.enums.PartCondition;
import tn.epaviste.enums.QuoteStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuoteResponse {
    private Long id;
    private Long rfqId;
    private Long sellerId;
    private String sellerName;
    private String sellerCompany;
    private BigDecimal price;
    private PartCondition condition;
    private Integer deliveryTime;
    private String shippingMethod;
    private String message;
    private QuoteStatus status;
    private LocalDateTime createdAt;
}
