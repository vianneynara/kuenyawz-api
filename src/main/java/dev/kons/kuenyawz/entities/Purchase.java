package dev.kons.kuenyawz.entities;

import dev.kons.kuenyawz.utils.idgenerator.SnowFlakeIdValue;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Purchase extends Auditables {
    @Id
    @SnowFlakeIdValue(name = "purchase_id")
    @Column(name = "purchase_id", columnDefinition = "BIGINT", updatable = false, nullable = false)
    private Long purchaseId;

    @SnowFlakeIdValue(name = "invoice_id")
    @Column(name = "invoice_id", columnDefinition = "BIGINT", updatable = false, nullable = false)
    private Long invoiceId;

    @Column
    private LocalDateTime orderDate;

    @Embedded
    private Coordinate coordinate;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dp_transaction_id", referencedColumnName = "transaction_id")
    private Transaction dpTransaction;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fp_transaction_id", referencedColumnName = "transaction_id")
    private Transaction fpTransaction;

    @OneToMany(mappedBy = "purchase", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PurchaseItem> purchaseItems;

    /**
     * Ongoing status (Process of making the product)
     */
    public enum OrderStatus {
        PENDING("Waiting for system"),
        WAITING_DOWN_PAYMENT("Waiting for down payment"),
        CONFIRMING("Waiting for confirmation from seller"),
        CONFIRMED("Confirmed by seller"),
        WAITING_SETTLEMENT("Waiting for settlement"),
        PROCESSING("Being processed"),
        DELIVERED("Order delivered"),
        CANCELLED("Order cancelled");

        private final String value;

        OrderStatus(String value) {
            this.value = value;
        }

        public static OrderStatus fromString(String value) {
            for (OrderStatus status : OrderStatus.values()) {
                if (status.value.equalsIgnoreCase(value)) {
                    return status;
                }
            }
            throw new IllegalArgumentException("Invalid status: " + value);
        }
    }
}
