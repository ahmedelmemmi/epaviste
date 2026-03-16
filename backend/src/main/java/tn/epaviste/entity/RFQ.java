package tn.epaviste.entity;

import jakarta.persistence.*;
import lombok.*;
import tn.epaviste.enums.PartCondition;
import tn.epaviste.enums.RFQStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "rfqs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RFQ {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_id", nullable = false)
    private User buyer;

    private String carBrand;
    private String carModel;
    private Integer carYear;
    private String vin;
    private String partName;
    private String partCategory;

    @Enumerated(EnumType.STRING)
    private PartCondition preferredCondition;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String location;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private RFQStatus status = RFQStatus.OPEN;

    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "rfq", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<RFQImage> images = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
