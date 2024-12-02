package dev.kons.kuenyawz.entities;

import dev.kons.kuenyawz.utils.idgenerator.SnowFlakeIdValue;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Apriori {

    @Id
    @SnowFlakeIdValue(name = "apriori_id")
    @Column
    private Long aprioriId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "base_product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "recommended_product1_id", nullable = false)
    private Product recommended1;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "recommended_product2_id", nullable = false)
    private Product recommended2;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "recommended_product3_id", nullable = false)
    private Product recommended3;
}
