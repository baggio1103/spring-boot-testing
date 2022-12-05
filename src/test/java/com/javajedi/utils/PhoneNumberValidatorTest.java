package com.javajedi.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class PhoneNumberValidatorTest {

    private PhoneNumberValidator underTest;

    @BeforeEach
    void setUp() {
        underTest = new PhoneNumberValidator();
    }

    @ParameterizedTest
    @CsvSource(value = {
            "+79869334644, true",
            "+99869334644, false",
            "+7986933464412, false"
    })
    void itShouldValidatePhoneNumber(String input, boolean expected) {
        // Given
        // When
        var isValid = underTest.test(input);

        // Then
        assertThat(isValid).isEqualTo(expected);
    }

}