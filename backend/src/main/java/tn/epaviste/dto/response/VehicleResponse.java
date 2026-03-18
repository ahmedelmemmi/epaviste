package tn.epaviste.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class VehicleResponse {
    private Long id;
    private String brand;
    private String model;
    private Integer year;
    private String vin;
    private LocalDateTime createdAt;
}
