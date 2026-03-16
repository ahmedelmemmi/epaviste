package tn.epaviste.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "rfq_images")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RFQImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rfq_id", nullable = false)
    private RFQ rfq;

    private String imageUrl;
}
