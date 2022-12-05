package com.javajedi.repository;

import com.javajedi.customer.Currency;
import com.javajedi.customer.Payment;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(properties =
        {
         "spring.jpa.properties.javax.persistence.validation.mode=none"
        }
)
class PaymentRepositoryTest {

    @Autowired
    private PaymentRepository underTest;

    @Test
    void itShouldInsertPayment() {
        // Given
        var paymentId = 1L;
        var customerId = UUID.randomUUID();
        var amount = BigDecimal.valueOf(999.99);
        var source = "debit card";
        var description = "Donation";
        var payment = new Payment(paymentId, customerId, amount, Currency.USD, source, description);

        // When
        underTest.save(payment);

        // Then
        Optional<Payment> optionalPayment = underTest.findById(paymentId);
        assertThat(optionalPayment).isPresent()
                .hasValueSatisfying(pay -> {
                    assertThat(pay.getPaymentId()).isEqualTo(paymentId);
                    assertThat(pay.getCustomerId()).isEqualTo(customerId);
                    assertThat(pay.getSource()).isEqualTo(source);
                    assertThat(pay.getDescription()).isEqualTo(description);
                });
    }

}