package tn.epaviste.dto.request;

import lombok.*;
import tn.epaviste.enums.UserStatus;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class UpdateUserStatusRequest {
    private UserStatus status;
}
