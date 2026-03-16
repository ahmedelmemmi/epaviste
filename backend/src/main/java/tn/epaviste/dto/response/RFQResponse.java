package tn.epaviste.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tn.epaviste.enums.PartCondition;
import tn.epaviste.enums.RFQStatus;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RFQResponse {
    private Long id;
    private Long buyerId;
    private String buyerName;
    private String carBrand;
    private String carModel;
    private Integer carYear;
    private String vin;
    private String partName;
    private String partCategory;
    private PartCondition preferredCondition;
    private String description;
    private String location;
    private RFQStatus status;
    private LocalDateTime createdAt;
    private List<String> images;
    private long quoteCount;
}
