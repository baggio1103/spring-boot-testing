package com.javajedi.controller;

import com.javajedi.customer.Payment;
import com.javajedi.data.PaymentRequest;
import com.javajedi.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/payments")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("")
    public void makePayment(@RequestBody PaymentRequest paymentRequest) {
        paymentService.chargeCard(paymentRequest.getPayment().getCustomerId(), paymentRequest);
    }

    @GetMapping
    public List<Payment> getPayments() {
        return paymentService.getAllPayments();
    }

}
