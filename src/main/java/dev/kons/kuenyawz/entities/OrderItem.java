package dev.kons.kuenyawz.entities;

import dev.kons.kuenyawz.utils.idgenerator.SnowFlakeIdValue;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderItem {
    @Id
    @SnowFlakeIdValue(name = "transaction_id")
    @Column(name = "order_item_id", columnDefinition = "BIGINT", updatable = false, nullable = false)
    private Long orderItemId;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order orderId;

    @Column
    private String note;

    @Column
    private Integer quantity;

    @Column(name = "bought_price")
    private BigDecimal boughtPrice;

    @ManyToOne
    @JoinColumn(name = "variant_id", nullable = false)
    private Variant variantId;
}
