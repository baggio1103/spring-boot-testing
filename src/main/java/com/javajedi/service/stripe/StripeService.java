package com.javajedi.service.stripe;

import com.javajedi.customer.Currency;
import com.javajedi.data.CardPaymentCharge;
import com.javajedi.repository.CardPaymentCharger;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.net.RequestOptions;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(value = "stripe.enabled", havingValue = "true")
public class StripeService implements CardPaymentCharger {

    private final StripeApi stripeApi;

    private final static RequestOptions requestOptions = RequestOptions.builder()
            .setApiKey("sk_test_4eC39HqLyjWDarjtT1zdp7dc")
            .build();

    @Override
    public CardPaymentCharge chargeCard(BigDecimal amount, Currency currency, String source, String description) {
        Map<String, Object> params = new HashMap<>();
        params.put("amount", amount);
        params.put("currency", currency);
        params.put("source", source);
        params.put("description", description);
        try {
            Charge charge = stripeApi.create(params, requestOptions);
            return new CardPaymentCharge(charge.getPaid());
        } catch (StripeException e) {
            throw new IllegalArgumentException("Cannot make stripe request");
        }
    }

}
