package dev.kons.kuenyawz.entities;

import dev.kons.kuenyawz.utils.idgenerator.SnowFlakeIdValue;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PurchaseItem {
    @Id
    @SnowFlakeIdValue(name = "purchase_item_id")
    @Column(name = "purchase_item_id", columnDefinition = "BIGINT", updatable = false, nullable = false)
    private Long purchaseItemId;

    @Column
    private String note;

    @Column(nullable = false)
    @Min(1)
    private Integer quantity;

    @Column(nullable = false)
    private BigDecimal boughtPrice;

	@Version
	private Long version;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "variant_id", nullable = false)
    private Variant variant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchase_id", nullable = false)
    private Purchase purchase;
}
