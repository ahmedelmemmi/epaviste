package tn.epaviste.dto.response;

import lombok.*;
import tn.epaviste.enums.UserRole;
import tn.epaviste.enums.UserStatus;

import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AdminUserResponse {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private UserRole role;
    private UserStatus status;
    private LocalDateTime createdAt;
}
