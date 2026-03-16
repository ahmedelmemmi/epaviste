package tn.epaviste.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import tn.epaviste.enums.PartCondition;

import java.util.List;

@Data
public class RFQRequest {

    @NotBlank(message = "Car brand is required")
    private String carBrand;

    @NotBlank(message = "Car model is required")
    private String carModel;

    @NotNull(message = "Car year is required")
    private Integer carYear;

    private String vin;

    @NotBlank(message = "Part name is required")
    private String partName;

    private String partCategory;

    private PartCondition preferredCondition;

    private String description;

    private String location;

    private List<String> imageUrls;
}
