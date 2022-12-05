package com.javajedi.service;

import com.javajedi.customer.Currency;
import com.javajedi.customer.Payment;
import com.javajedi.data.PaymentRequest;
import com.javajedi.repository.CardPaymentCharger;
import com.javajedi.repository.CustomerRepository;
import com.javajedi.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import static com.javajedi.customer.Currency.RUBLE;
import static com.javajedi.customer.Currency.USD;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final static List<Currency> ACCEPTED_CURRENCIES = List.of(RUBLE, USD);

    private final PaymentRepository paymentRepository;

    private final CustomerRepository customerRepository;

    private final CardPaymentCharger cardPaymentCharger;

    @SneakyThrows
    public void chargeCard(UUID customerId, PaymentRequest paymentRequest) {
        // Checking whether customer is present or not!
        // if not throw an exception
        customerRepository.findById(customerId).ifPresentOrElse(
                customer -> {
                    var payment = paymentRequest.getPayment();
                    boolean isCurrencyAccepted = ACCEPTED_CURRENCIES
                            .stream()
                            .anyMatch(currency -> payment.getCurrency().equals(currency));
                    // Checking whether currency is accepted or not, if not -> throw an exception
                    if (!isCurrencyAccepted) {
                        throw new IllegalArgumentException(String.format("Currency - %s is not supported currently!",
                                payment.getCurrency()));
                    }
                    var cardPaymentCharge = cardPaymentCharger.chargeCard(payment.getAmount(),
                            payment.getCurrency(),
                            payment.getSource(),
                            payment.getDescription());
                    // Checking whether card is debited or not, if not -> throw an exception
                    if (!cardPaymentCharge.isCardDebited()) {
                        throw new IllegalArgumentException("Card is not charged");
                    }
                    // If all checks are successful, payment has gone successful
                    payment.setCustomerId(customerId);
                    paymentRepository.save(payment);
                }, () -> {
                   throw new IllegalArgumentException("Customer not found with id: " + customerId);
                }
        );
    }

    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

}
