package com.javajedi.customer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

import static javax.persistence.GenerationType.IDENTITY;

@Getter
@Setter
@Entity
@Table(name = "payments")
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long paymentId;

    @Column(name = "customer_id", nullable = false)
    private UUID customerId;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Column(name = "currency", nullable = false)
    @Enumerated(EnumType.STRING)
    private Currency currency;

    @Column(name = "source", nullable = false)
    private String source;

    @Column(name = "description", nullable = false)
    private String description;

    public Payment(BigDecimal amount, Currency currency, String source, String description) {
        this.amount = amount;
        this.currency = currency;
        this.source = source;
        this.description = description;
    }
}
