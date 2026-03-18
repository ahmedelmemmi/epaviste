package tn.epaviste.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import tn.epaviste.enums.OrderStatus;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UpdateOrderStatusRequest {
    @NotNull
    private OrderStatus status;
}
