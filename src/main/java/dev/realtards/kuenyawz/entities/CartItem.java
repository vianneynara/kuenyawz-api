package dev.realtards.kuenyawz.entities;

import dev.realtards.kuenyawz.dtos.cartItem.CartItemPatchDto;
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
    private Integer quantity;

    @Column
    private String note;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "variant_id", nullable = false)
    private Variant variant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    public CartItem patchFromDto(CartItemPatchDto cartItemPatchDto) {
        return patchFromDto(cartItemPatchDto, this.variant);
    }

    public CartItem patchFromDto(CartItemPatchDto cartItemPatchDto, Variant variant) {
        if (variant != null && !variant.equals(this.variant))
            this.variant = variant;

        if (cartItemPatchDto.getQuantity() != null)
            this.quantity = cartItemPatchDto.getQuantity();

        if (cartItemPatchDto.getNote() != null)
            this.note = cartItemPatchDto.getNote();

        return this;
    }
}
