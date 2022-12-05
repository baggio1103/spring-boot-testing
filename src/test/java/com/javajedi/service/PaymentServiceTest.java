package com.javajedi.service;

import com.javajedi.customer.Currency;
import com.javajedi.customer.Customer;
import com.javajedi.customer.Payment;
import com.javajedi.data.CardPaymentCharge;
import com.javajedi.data.PaymentRequest;
import com.javajedi.repository.CardPaymentCharger;
import com.javajedi.repository.CustomerRepository;
import com.javajedi.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

class PaymentServiceTest {

    @Mock
    private CardPaymentCharger cardPaymentCharger;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @Captor
    private ArgumentCaptor<Payment> argumentCaptor;

    private PaymentService underTest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        underTest = new PaymentService(paymentRepository, customerRepository, cardPaymentCharger);
    }

    @Test
    void itShouldChargeCardSuccessfully() {
        // Given
        var customerId = UUID.randomUUID();
        given(customerRepository.findById(customerId)).willReturn(Optional.of(mock(Customer.class)));

        var currency = Currency.RUBLE;
        var payment = new Payment(BigDecimal.valueOf(199.99), currency,  "Debit card", "Donation");
        var paymentRequest = new PaymentRequest(payment);

        // Card is charged successfully
        given(cardPaymentCharger.chargeCard(payment.getAmount(),
                payment.getCurrency(),
                payment.getSource(),
                payment.getDescription())).willReturn(new CardPaymentCharge(true));

        // When
        underTest.chargeCard(customerId, paymentRequest);
        // Then
        then(paymentRepository).should().save(argumentCaptor.capture());
        var value = argumentCaptor.getValue();
        assertThat(value.getCustomerId()).isEqualTo(customerId);
        assertThat(value.getAmount()).isEqualTo(payment.getAmount());
        assertThat(value.getDescription()).isEqualTo(payment.getDescription());
    }

    @Test
    void itShouldThrowExceptionIfCardNotDebited() {
        // Given
        var customerId = UUID.randomUUID();
        given(customerRepository.findById(customerId)).willReturn(Optional.of(mock(Customer.class)));

        var currency = Currency.RUBLE;
        var payment = new Payment(BigDecimal.valueOf(199.99), currency,  "Debit card", "Donation");
        var paymentRequest = new PaymentRequest(payment);

        // Card is not charged successfully
        given(cardPaymentCharger.chargeCard(payment.getAmount(),
                payment.getCurrency(),
                payment.getSource(),
                payment.getDescription())).willReturn(new CardPaymentCharge(false));

        // When
        // Then
        assertThatThrownBy(() -> underTest.chargeCard(customerId, paymentRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Card is not charged");

        // PaymentRepository has no interactions
        then(paymentRepository).shouldHaveNoInteractions();
    }


    @Test
    void itShouldFailWhenCustomerIsNotFound() {
        // Given
        var customerId = UUID.randomUUID();
        given(customerRepository.findById(customerId)).willReturn(Optional.empty());

        var currency = Currency.RUBLE;
        var payment = new Payment(BigDecimal.valueOf(199.99), currency,  "Debit card", "Donation");
        var paymentRequest = new PaymentRequest(payment);

        // When
        assertThatThrownBy(() -> underTest.chargeCard(customerId, paymentRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(String.format("Customer not found with id: %s", customerId));

        //Then
        then(paymentRepository).should(never()).save(any(Payment.class));

        // PaymentRepository has no interactions nor CardPaymentCharger
        then(paymentRepository).shouldHaveNoInteractions();

        then(cardPaymentCharger).shouldHaveNoInteractions();
    }

    @Test
    void itShouldFailWhenCurrencyIsNotSupported() {
        // Given
        var customerId = UUID.randomUUID();
        given(customerRepository.findById(customerId)).willReturn(Optional.of(mock(Customer.class)));

        var currency = Currency.EURO;
        var payment = new Payment(BigDecimal.valueOf(199.99), currency,  "Debit card", "Donation");
        var paymentRequest = new PaymentRequest(payment);

        // When
        assertThatThrownBy(() -> underTest.chargeCard(customerId, paymentRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(String.format("Currency - %s is not supported currently!",
                        currency));
        then(paymentRepository).should(never()).save(any(Payment.class));
        then(cardPaymentCharger).shouldHaveNoInteractions();
        then(paymentRepository).shouldHaveNoInteractions();
    }

}