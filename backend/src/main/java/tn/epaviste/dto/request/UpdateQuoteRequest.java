package tn.epaviste.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import tn.epaviste.enums.PartCondition;
import java.math.BigDecimal;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UpdateQuoteRequest {
    @NotNull
    @DecimalMin("0.01")
    private BigDecimal price;
    private PartCondition condition;
    private Integer deliveryTime;
    private String shippingMethod;
    private String message;
}
