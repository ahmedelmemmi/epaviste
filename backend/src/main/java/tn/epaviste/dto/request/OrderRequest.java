package tn.epaviste.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderRequest {

    @NotNull(message = "Quote ID is required")
    private Long quoteId;
}
