package tn.epaviste.dto.request;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class BroadcastNotificationRequest {
    private String type;
    private String message;
    /** Optional: BUYER, SELLER, ADMIN. If null, sends to all users. */
    private String targetRole;
}
