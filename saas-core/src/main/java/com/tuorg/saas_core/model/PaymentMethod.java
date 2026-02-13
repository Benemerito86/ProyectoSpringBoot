package com.tuorg.saas_core.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "payment_type")
@Data
public abstract class PaymentMethod {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User owner;
}

@Entity
@DiscriminatorValue("CARD")
class CreditCard extends PaymentMethod {
    private String last4Digits;
}

@Entity
@DiscriminatorValue("PAYPAL")
class PayPal extends PaymentMethod {
    private String emailAccount;
}

@Entity
@DiscriminatorValue("TRANSFER")
class BankTransfer extends PaymentMethod {
    private String iban;
    private String swift;
}
