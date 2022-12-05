package com.javajedi.data;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CardPaymentCharge {

    private final Boolean isCardDebited;

    public Boolean isCardDebited() {
        return isCardDebited;
    }

}
