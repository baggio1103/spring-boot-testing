package com.javajedi.repository;

import com.javajedi.customer.Currency;
import com.javajedi.data.CardPaymentCharge;

import java.math.BigDecimal;

public interface CardPaymentCharger {

    CardPaymentCharge chargeCard(
            BigDecimal amount,
            Currency currency,
            String source,
            String description
    );

}
