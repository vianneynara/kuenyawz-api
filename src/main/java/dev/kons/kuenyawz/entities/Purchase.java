package dev.kons.kuenyawz.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import dev.kons.kuenyawz.utils.idgenerator.SnowFlakeIdValue;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Table(indexes = {
    @Index(name = "idx_purchase_status", columnList = "status"),
    @Index(name = "idx_purchase_orderdate", columnList = "order_date")
})
public class Purchase extends Auditables {
    @Id
    @SnowFlakeIdValue(name = "purchase_id")
    @Column(name = "purchase_id", columnDefinition = "BIGINT", updatable = false, nullable = false)
    private Long purchaseId;

    @Column
    private String fullAddress;

    @Column
    private LocalDate purchaseDate;

    @Embedded
    private Coordinate coordinate;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PurchaseStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dp_transaction_id", referencedColumnName = "transaction_id")
    private Transaction dpTransaction;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fp_transaction_id", referencedColumnName = "transaction_id")
    private Transaction fpTransaction;

    @OneToMany(mappedBy = "purchase", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PurchaseItem> purchaseItems;

    /**
     * Ongoing status.
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Getter
    public enum PurchaseStatus {
        @JsonProperty("PENDING")
        PENDING("Waiting for system"),

        @JsonProperty("WAITING_DOWN_PAYMENT")
        WAITING_DOWN_PAYMENT("Waiting for down payment"),

        @JsonProperty("CONFIRMING")
        CONFIRMING("Waiting for confirmation from seller"),

        @JsonProperty("CONFIRMED")
        CONFIRMED("Confirmed by seller"),

        @JsonProperty("WAITING_SETTLEMENT")
        WAITING_SETTLEMENT("Waiting for settlement"),

        @JsonProperty("PROCESSING")
        PROCESSING("Being processed"),

        @JsonProperty("DELIVERED")
        DELIVERED("Purchase delivered"),

        @JsonProperty("CANCELLED")
        CANCELLED("Purchase cancelled");

        private final String description;

        PurchaseStatus(String description) {
            this.description = description;
        }

        @JsonValue
        public String getStatus() {
            return name();
        }

        @JsonCreator
        public static PurchaseStatus fromString(String value) {
            for (PurchaseStatus status : PurchaseStatus.values()) {
                if (status.name().equalsIgnoreCase(value)) {
                    return status;
                }
            }
            throw new IllegalArgumentException("Invalid status: " + value);
        }
    }
}
