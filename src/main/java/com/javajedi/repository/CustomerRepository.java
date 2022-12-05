package com.javajedi.repository;

import com.javajedi.customer.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;


public interface CustomerRepository extends JpaRepository<Customer, UUID> {

    @Query(value = """
            SELECT id, name, phone_number 
            FROM customers WHERE phone_number = :phone_number
            """, nativeQuery = true)
    Optional<Customer> selectCustomerByPhoneNumber(@Param("phone_number") String phoneNumber);

}
