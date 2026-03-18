package tn.epaviste.dto.response;

import lombok.*;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class BuyerStatsResponse {
    private long totalRFQsSubmitted;
    private long quotesReceived;
    private long activeOrders;
    private long completedOrders;
    private List<NotificationResponse> recentNotifications;
}
