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

    @Column
    private Long productId;

    @Column
    private Long recommended1;

    @Column
    private Long recommended2;

    @Column
    private Long recommended3;
}
