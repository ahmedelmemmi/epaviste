package tn.epaviste.dto.response;

import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SellerEarningsResponse {
    private BigDecimal totalEarnings;
    private BigDecimal totalCommission;
    private BigDecimal netEarnings;
    private BigDecimal pendingPayouts;
    private List<PaymentResponse> paymentHistory;
}
