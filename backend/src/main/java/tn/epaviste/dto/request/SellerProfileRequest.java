package tn.epaviste.dto.request;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SellerProfileRequest {
    private String companyName;
    private String address;
    private String description;
    private String phone;
    private String deliveryZones;
    private String shippingMethods;
}
