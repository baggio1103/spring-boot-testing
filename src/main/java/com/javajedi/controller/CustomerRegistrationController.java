package com.javajedi.controller;

import com.javajedi.customer.Customer;
import com.javajedi.data.CustomerRegistrationRequest;
import com.javajedi.service.CustomerRegistrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/customers")
public class CustomerRegistrationController {

    private final CustomerRegistrationService customerRegistrationService;

    @PutMapping("")
    public void registerNewCustomer(@Valid @RequestBody CustomerRegistrationRequest request) {
        customerRegistrationService.registerNewCustomer(request);
    }

    @GetMapping("/{customerId}")
    public Customer getCustomer(@PathVariable("customerId") String customerId) {
        return customerRegistrationService.getCustomerByCustomerId(UUID.fromString(customerId));
    }


}