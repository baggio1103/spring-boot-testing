package com.javajedi.data;

import com.javajedi.customer.Customer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class CustomerRegistrationRequest {

    private Customer customer;

}
