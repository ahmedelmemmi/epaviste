package tn.epaviste.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import tn.epaviste.enums.PartCondition;

import java.math.BigDecimal;

@Data
public class QuoteRequest {

    @NotNull(message = "RFQ ID is required")
    private Long rfqId;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be positive")
    private BigDecimal price;

    private PartCondition condition;

    private Integer deliveryTime;

    private String shippingMethod;

    private String message;
}
