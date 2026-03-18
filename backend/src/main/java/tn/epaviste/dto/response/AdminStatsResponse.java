package tn.epaviste.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AdminStatsResponse {
    private long totalUsers;
    private long totalBuyers;
    private long totalSellers;
    private long activeSellers;
    private long totalRFQs;
    private long totalQuotes;
    private long totalOrdersCompleted;
    private BigDecimal totalGMV;
    private BigDecimal totalCommissionRevenue;
    private double rfqToOrderConversionRate;
}
