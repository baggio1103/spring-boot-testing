package com.javajedi.service;

import com.javajedi.customer.Customer;
import com.javajedi.data.CustomerRegistrationRequest;
import com.javajedi.repository.CustomerRepository;
import com.javajedi.utils.PhoneNumberValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomerRegistrationService {

    private final CustomerRepository customerRepository;

    private final PhoneNumberValidator phoneNumberValidator;

    public void registerNewCustomer(CustomerRegistrationRequest request) {
        var requestCustomer = request.getCustomer();

        if (!phoneNumberValidator.test(requestCustomer.getPhoneNumber())) {
            throw new IllegalArgumentException(String.format("Phone number [%s] is not valid", requestCustomer.getPhoneNumber()));
        }

        Optional<Customer> optionalCustomer = customerRepository
                .selectCustomerByPhoneNumber(requestCustomer.getPhoneNumber());
        optionalCustomer.ifPresentOrElse(customer -> {
            if (requestCustomer.getName().equals(customer.getName())) {
                return;
            }
            throw new IllegalArgumentException(
                    String.format("PhoneNumber [%s] is already taken...", requestCustomer.getPhoneNumber()));
        }, () ->
                {
                    if (requestCustomer.getId() == null) {
                        requestCustomer.setId(UUID.randomUUID());
                    }
                    customerRepository.save(requestCustomer);
                }
        );
    }

    public Customer getCustomerByCustomerId(UUID customerId) {
       return customerRepository.findById(customerId).orElseThrow( () ->
                new IllegalArgumentException(String.format("Customer with id [%s] not found", customerId))
        );
    }

}
