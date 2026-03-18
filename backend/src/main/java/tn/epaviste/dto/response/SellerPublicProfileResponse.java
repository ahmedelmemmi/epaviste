package tn.epaviste.dto.response;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SellerPublicProfileResponse {
    private Long sellerId;
    private String name;
    private String companyName;
    private Boolean verified;
    private Double rating;
    private Long reviewCount;
    private String deliveryZones;
}
