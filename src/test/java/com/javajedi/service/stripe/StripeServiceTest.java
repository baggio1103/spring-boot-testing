package com.javajedi.service.stripe;

import com.javajedi.customer.Currency;
import com.stripe.exception.ApiException;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.net.RequestOptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

class StripeServiceTest {

    private StripeService underTest;

    @Mock
    private StripeApi stripeApi;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        underTest = new StripeService(stripeApi);
    }

    @Test
    void itShouldChargeCard() throws StripeException {
        // Given
        var amount = BigDecimal.valueOf(199.99);
        var source = "0x0x0x0x0";
        var currency = Currency.USD;
        var description = "Zakat";

        var charge = new Charge();
        charge.setPaid(true);

        given(stripeApi.create(anyMap(), any())).willReturn(charge);
        // When
        underTest.chargeCard(amount, currency, source, description);

        // Then
        ArgumentCaptor<Map<String, Object>> mapArgumentCaptor = ArgumentCaptor.forClass(Map.class);
        ArgumentCaptor<RequestOptions> optionsArgumentCaptor = ArgumentCaptor.forClass(RequestOptions.class);
        then(stripeApi).should().create(mapArgumentCaptor.capture(), optionsArgumentCaptor.capture());
        var params = mapArgumentCaptor.getValue();
        var options = optionsArgumentCaptor.getValue();
        assertThat(params.keySet()).hasSize(4);
        assertThat(params.get("amount")).isEqualTo(amount);
        assertThat(params.get("currency")).isEqualTo(currency);
        assertThat(params.get("description")).isEqualTo(description);
        assertThat(params.get("source")).isEqualTo(source);

        assertThat(options).isNotNull();
    }

    @Test
    void itShouldThrowExceptionWhenStripeApiCallMade() throws StripeException {
        // Given
        var amount = BigDecimal.valueOf(199.99);
        var source = "0x0x0x0x0";
        var currency = Currency.USD;
        var description = "Gift";

        given(stripeApi.create(anyMap(), any())).willThrow(new ApiException("Exception", null, null, null, null));
        // When
        assertThatThrownBy(() -> underTest.chargeCard(amount, currency, source, description))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Cannot make stripe request");
    }

}