package tn.epaviste.dto.response;

import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SellerStatsResponse {
    private long totalRFQsReceived;
    private long activeQuotes;
    private long ordersInProgress;
    private BigDecimal totalRevenue;
    private Double rating;
    private List<NotificationResponse> recentNotifications;
}
