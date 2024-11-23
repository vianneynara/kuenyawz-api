package dev.realtards.kuenyawz.entities;

import dev.realtards.kuenyawz.utils.idgenerator.SnowFlakeIdValue;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Orders extends Auditables {
    @Id
    @SnowFlakeIdValue(name = "order_id")
    @Column(name = "order_id", columnDefinition = "BIGINT", updatable = false, nullable = false)
    private Long orderId;

    @SnowFlakeIdValue(name = "invoice_id")
    @Column(name = "invoice_id", columnDefinition = "BIGINT", updatable = false, nullable = false)
    private Long invoiceId;

    @Column
    private LocalDateTime orderDate;

    @Column
    private String address;

    @Column
    private Long coordinates;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dp_reference_id", referencedColumnName = "reference_id")
    private Transactions dpReferenceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fp_reference_id", referencedColumnName = "reference_id")
    private Transactions fpReferenceId;

    @Setter
    @Getter
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OrderStatus status;

    /**
     * Ongoing status (Process of making the product)
     */
    public enum OrderStatus {
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
