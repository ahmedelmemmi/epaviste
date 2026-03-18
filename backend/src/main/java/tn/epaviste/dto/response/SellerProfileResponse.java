package tn.epaviste.dto.response;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SellerProfileResponse {
    private Long id;
    private Long userId;
    private String name;
    private String email;
    private String phone;
    private String companyName;
    private String address;
    private String description;
    private Double rating;
    private Boolean verified;
    private String deliveryZones;
    private String shippingMethods;
    private Boolean messagingEnabled;
}
