package tn.epaviste.dto.response;

import lombok.*;
import tn.epaviste.enums.DisputeStatus;

import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AdminDisputeResponse {
    private Long id;
    private Long orderId;
    private Long complainantId;
    private String complainantName;
    private String reason;
    private DisputeStatus status;
    private String resolution;
    private LocalDateTime createdAt;
    private LocalDateTime resolvedAt;
}
