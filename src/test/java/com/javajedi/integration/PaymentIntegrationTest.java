package com.javajedi.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.javajedi.customer.Customer;
import com.javajedi.customer.Payment;
import com.javajedi.data.CustomerRegistrationRequest;
import com.javajedi.data.PaymentRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static com.javajedi.customer.Currency.RUBLE;
import static java.util.Objects.requireNonNull;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PaymentIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void itShouldCreatePaymentSuccessfully() throws Exception {
        // Given
        var customerId = UUID.randomUUID();
        var name = "Baggio";
        var phoneNumber = "+79979331633";
        var customer = new Customer(customerId, name, phoneNumber);
        var customerRequest = new CustomerRegistrationRequest(customer);
        // When customer is saved in the db
        ResultActions customerResultActions = mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requireNonNull(objectToJson(customerRequest)))
        );

        // Given
        // A payment request
        var payment = new Payment(
                1L,
                customerId,
                BigDecimal.valueOf(1999.99),
                RUBLE,
                "debit card",
                "charity");
        var paymentRequest = new PaymentRequest(payment);

        // When a payment request is sent
        ResultActions paymentResults = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requireNonNull(objectToJson(paymentRequest)))
        );

        // Then
        customerResultActions.andExpect(status().isOk());
        paymentResults.andExpect(status().isOk());

        // Retrieve customer and payment to check
        ResultActions customerGetResult = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/v1/customers/{customerId}", customerId
                ).contentType(MediaType.APPLICATION_JSON)
                .content(requireNonNull(objectToJson(paymentRequest)))
        );


        ResultActions paymentsResponse = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/payments")
                .contentType(MediaType.APPLICATION_JSON));


        // Then
        customerGetResult.andExpect(status().isOk());

        paymentsResponse.andExpect(status().isOk());

        var payments = jsonToObject(
                paymentsResponse.andReturn().getResponse().getContentAsString(),
                new TypeReference<List<Payment>>() {}
                );

        assertThat(payments).isNotNull();
        assertThat(payments.size()).isEqualTo(1);
        var paymentResponse = payments.get(0);
        assertThat(paymentResponse.getCustomerId()).isEqualTo(customerId);
        assertThat(paymentResponse.getPaymentId()).isEqualTo(1L);
        assertThat(paymentResponse.getAmount()).isEqualTo(BigDecimal.valueOf(1999.99));
        assertThat(paymentResponse.getDescription()).isEqualTo("charity");
        assertThat(paymentResponse.getSource()).isEqualTo("debit card");
        assertThat(paymentResponse.getCurrency()).isEqualTo(RUBLE);

        var customerObject = jsonToObject(
                customerGetResult.andReturn().getResponse().getContentAsString(),
                new TypeReference<Customer>() {}
                );

        assertThat(customerObject).isNotNull();
        assertThat(customerObject.getId()).isEqualTo(customerId);
        assertThat(customerObject.getName()).isEqualTo(name);
        assertThat(customerObject.getPhoneNumber()).isEqualTo(phoneNumber);
    }

    private <T> String objectToJson(T t) {
        try {
            return new ObjectMapper().writeValueAsString(t);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    private <T> T jsonToObject(String json, TypeReference<T> typeReference) {
        try {
            return new ObjectMapper().readValue(json, typeReference);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

}
