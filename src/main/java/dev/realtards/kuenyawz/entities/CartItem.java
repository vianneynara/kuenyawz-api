package dev.realtards.kuenyawz.entities;

import dev.realtards.kuenyawz.utils.idgenerator.SnowFlakeIdValue;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {
    @Id
    @SnowFlakeIdValue(name = "cart_item_id")
    @Column(name = "cart_item_id", columnDefinition = "BIGINT", updatable = false, nullable = false)
    private Long cartItemId;

    @Column
    private int quantity;

    @Column
    private String note;

    @ManyToOne
    @JoinColumn(name = "variant_id", nullable = false)
    private Variant variantId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;
}
