package dev.realtards.kuenyawz.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import dev.realtards.kuenyawz.utils.idgenerator.SnowFlakeIdValue;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CollectionId;
import org.springframework.data.domain.Auditable;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
//@EqualsAndHashCode(callSuper = true, exclude = "product")
//@ToString(callSuper = true, exclude = "product")
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
    private Long coordinates;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dp_reference_id", referencedColumnName = "reference_id")
    private Transactions dpReferenceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fp_reference_id", referencedColumnName = "reference_id")
    private Transactions fpReferenceId;

    public enum OrderStatus {
        DEPOSIT, FULL_PAID
    }

    @Setter
    @Getter
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OrderStatus status;
}
