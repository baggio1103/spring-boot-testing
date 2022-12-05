package com.javajedi.service;

import com.javajedi.customer.Customer;
import com.javajedi.data.CustomerRegistrationRequest;
import com.javajedi.repository.CustomerRepository;
import com.javajedi.utils.PhoneNumberValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

class CustomerRegistrationServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private PhoneNumberValidator phoneNumberValidator;

    private CustomerRegistrationService underTest;

    @Captor
    private ArgumentCaptor<Customer> argumentCaptor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        underTest = new CustomerRegistrationService(customerRepository, phoneNumberValidator);
    }

    @Test
    void itShouldRegisterNewCustomer() {
        // Given a customer
        var id = UUID.randomUUID();
        var phoneNumber = "+79869334644";
        var name = "Baggio";
        var customer = new Customer(id, name, phoneNumber);

        // a request
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(customer);

        given(phoneNumberValidator.test(phoneNumber)).willReturn(true);

        // given
        given(customerRepository.selectCustomerByPhoneNumber(phoneNumber)).willReturn(Optional.empty());

        // When
        underTest.registerNewCustomer(request);

        then(customerRepository).should().save(argumentCaptor.capture());
        var customerArgumentCapture = argumentCaptor.getValue();
        assertThat(customerArgumentCapture).isEqualToComparingFieldByField(customer);
    }

    @Test
    void itShouldNotSave() {
        // Given
        var id = UUID.randomUUID();
        var phoneNumber = "+79869334644";
        var name = "Baggio";
        var customer = new Customer(id, name, phoneNumber);

        CustomerRegistrationRequest request = new CustomerRegistrationRequest(customer);

        given(phoneNumberValidator.test(phoneNumber)).willReturn(true);

        given(customerRepository.selectCustomerByPhoneNumber(phoneNumber)).willReturn(Optional.of(customer));

        // When
        underTest.registerNewCustomer(request);

        //Then
        then(customerRepository).should(never()).save(customer);
        then(customerRepository).should().selectCustomerByPhoneNumber(phoneNumber);
        then(customerRepository).shouldHaveNoMoreInteractions();
    }

    @Test
    void itShouldThrowExceptionWhenSavingNewCustomer() {
        // Given
        var id = UUID.randomUUID();
        var phoneNumber = "+79869334644";
        var name = "Baggio";
        var customer = new Customer(id, name, phoneNumber);

        CustomerRegistrationRequest request = new CustomerRegistrationRequest(customer);

        given(phoneNumberValidator.test(phoneNumber)).willReturn(true);

        given(customerRepository.selectCustomerByPhoneNumber(phoneNumber))
                .willReturn(Optional.of(new Customer(id, "Marc", phoneNumber)));

        // When
        // Then
        assertThatThrownBy(() -> underTest.registerNewCustomer(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("PhoneNumber [+79869334644] is already taken...");

        // Finally
        then(customerRepository).should(never()).save(customer);
    }


    @Test
    void itShouldSaveWhenIdIsNull() {
        // Given
        var phoneNumber = "+79869334644";
        var name = "Baggio";
        var customer = new Customer(null, name, phoneNumber);

        CustomerRegistrationRequest request = new CustomerRegistrationRequest(customer);

        given(phoneNumberValidator.test(phoneNumber)).willReturn(true);

        given(customerRepository.selectCustomerByPhoneNumber(phoneNumber))
                .willReturn(Optional.empty());

        // When
        underTest.registerNewCustomer(request);
        // Then
        then(customerRepository).should().save(argumentCaptor.capture());
        var value = argumentCaptor.getValue();
        assertThat(value.getName()).isEqualTo(customer.getName());
        assertThat(value.getPhoneNumber()).isEqualTo(customer.getPhoneNumber());
        assertThat(value.getId()).isNotNull();
    }

    @Test
    void itShouldFailWhenNameIsNull() {
        // Given
        var id = UUID.randomUUID();
        var customer = new Customer(id, null, "+79869334644");
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(customer);

        given(phoneNumberValidator.test(customer.getPhoneNumber())).willReturn(true);

        given(customerRepository.save(customer)).willThrow(DataIntegrityViolationException.class);
        // When
        // Then
        doThrow(DataIntegrityViolationException.class)
                .when(customerRepository).save(customer);
        assertThatThrownBy(() -> underTest.registerNewCustomer(request))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void itShouldReturnCustomerById() {
        // Given
        var id = UUID.randomUUID();
        given(customerRepository.findById(id)).willReturn(Optional.of(mock(Customer.class)));
        // When
        // Then
        assertThat(underTest.getCustomerByCustomerId(id)).isNotNull();
    }

    @Test
    void itShouldThrowExceptionWhenCustomerNotFoundWithId() {
        // Given
        var id = UUID.randomUUID();
        given(customerRepository.findById(id)).willReturn(Optional.empty());
        // When
        // Then
        assertThatThrownBy(() -> underTest.getCustomerByCustomerId(id))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(String.format("Customer with id [%s] not found", id));
    }

}