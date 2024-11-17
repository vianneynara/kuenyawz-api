package dev.realtards.kuenyawz.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import dev.realtards.kuenyawz.utils.idgenerator.SnowFlakeIdValue;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CollectionId;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
//@EqualsAndHashCode(callSuper = true, exclude = "product")
//@ToString(callSuper = true, exclude = "product")
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
