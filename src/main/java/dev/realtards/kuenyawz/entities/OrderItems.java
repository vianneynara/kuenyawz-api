package dev.realtards.kuenyawz.entities;

import dev.realtards.kuenyawz.utils.idgenerator.SnowFlakeIdValue;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItems {
    @Id
    @SnowFlakeIdValue(name = "transaction_id")
    @Column(name = "order_item_id", columnDefinition = "BIGINT", updatable = false, nullable = false)
    private Long OrderItemId;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Orders orderId;

    @Column
    private String note;

    @Column
    private Long quantity;

    @Column(name = "bought_price")
    private BigDecimal boughtPrice;

    @ManyToOne
    @JoinColumn(name = "variant_id", nullable = false)
    private Variant variantId;
}
