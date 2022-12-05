package com.javajedi.service.stripe;

import com.javajedi.customer.Currency;
import com.javajedi.data.CardPaymentCharge;
import com.javajedi.repository.CardPaymentCharger;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@ConditionalOnProperty(value = "stripe.enabled", havingValue = "false")
public class MockStripeService implements CardPaymentCharger {

    @Override
    public CardPaymentCharge chargeCard(BigDecimal amount, Currency currency, String source, String description) {
        return new CardPaymentCharge(true);
    }

}
