package com.javajedi.repository;

import com.javajedi.customer.Customer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@DataJpaTest(
        properties = {
                "spring.jpa.properties.javax.persistence.validation.mode=none"
        }
)
class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository underTest;

    @Test
    void itShouldSelectCustomerByPhoneNumber() {
        // Given
        var id = UUID.randomUUID();
        var phoneNumber = "0121";
        var name = "Baggio";
        var customer = new Customer(id, name, phoneNumber);

        // When
        underTest.save(customer);

        // Then
        Optional<Customer> optionalCustomer = underTest.selectCustomerByPhoneNumber(phoneNumber);
        assertThat(optionalCustomer).isPresent()
                .hasValueSatisfying(cus -> {
                    assertThat(cus.getPhoneNumber()).isEqualTo(phoneNumber);
                    assertThat(cus.getId()).isEqualTo(id);
                    assertThat(cus.getName()).isEqualTo(name);
                });
    }

    @Test
    void itShouldSelectCustomerByPhoneNumberWhenCustomerDoesNotExist() {
        // Given
        var phoneNumber = "0121";
        // When
        var optionalCustomer = underTest.selectCustomerByPhoneNumber(phoneNumber);
        // Then
        assertThat(optionalCustomer).isNotPresent();
    }

    @Test
    void itShouldSelectCustomerByPhoneNumberWhenPhoneNumberDoesNotExist() {
        // Given
        var id = UUID.randomUUID();
        var phoneNumber = "0121";
        var name = "Baggio";
        var customer = new Customer(id, name, phoneNumber);
        underTest.save(customer);

        // When
        var optionalCustomer = underTest.selectCustomerByPhoneNumber(phoneNumber);

        // Then
        assertThat(optionalCustomer).isPresent();
        assertThat(optionalCustomer).isPresent().hasValueSatisfying(
                c -> {
                    assertThat(c.getPhoneNumber()).isEqualTo(phoneNumber);
                    assertThat(c.getName()).isEqualTo(name);
                }
        );
    }

    @Test
    void itShouldSaveCustomer() {
        // Given
        var id = UUID.randomUUID();
        var customer = new Customer(id, "Baggio", "3108");

        // When
        underTest.save(customer);

        // Then
        Optional<Customer> optionalCustomer = underTest.findById(id);
        assertThat(optionalCustomer).isPresent();
        assertThat(optionalCustomer).isPresent()
                .hasValueSatisfying(c -> {
                    assertThat(c.getId()).isEqualTo(id);
                    assertThat(c.getName()).isEqualTo("Baggio");
                    assertThat(c.getPhoneNumber()).isEqualTo("3108");
                        }
                );
    }

    @Test
    void itShouldNotSaveCustomerWhenNameIsNull() {
        // Given
        var id = UUID.randomUUID();
        var customer = new Customer(id, null, "3108");

        // When
        // Then
        assertThatThrownBy(() -> underTest.save(customer))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void itShouldNotSaveCustomerWhenPhoneNumberIsNull() {
        // Given
        var id = UUID.randomUUID();
        var customer = new Customer(id, "baggio", null);

        // When
        // Then
        assertThatThrownBy(() -> underTest.save(customer))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void itShouldReturnCustomerById() {
        // Given
        var id = UUID.randomUUID();
        var phoneNumber = "0121";
        var name = "Baggio";

        // When
        underTest.save(new Customer(id, name, phoneNumber));

        // Then
        var optionalCustomer = underTest.findById(id);
        assertThat(optionalCustomer).isPresent()
                .hasValueSatisfying(customer -> {
                    assertThat(customer.getId()).isEqualTo(id);
                    assertThat(customer.getName()).isEqualTo(name);
                    assertThat(customer.getPhoneNumber()).isEqualTo(phoneNumber);
                });
    }

}