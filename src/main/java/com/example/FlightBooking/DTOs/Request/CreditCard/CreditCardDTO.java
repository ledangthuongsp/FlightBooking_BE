package com.example.FlightBooking.DTOs.Request.CreditCard;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreditCardDTO {
    private String stripePaymentMethodId;
    private String cardNumber;
    private String expirationDate;
    private String cvv;
    private String postalCode;
}
