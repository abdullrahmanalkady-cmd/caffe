package com.university.cafeteria.service;

import com.university.cafeteria.interfaces.IPaymentProcessor;
import com.university.cafeteria.model.User;

public class CashPaymentProcessor implements IPaymentProcessor {
    @Override
    public boolean processPayment(User student, double amount) {
        System.out.println("Payment of " + amount + " EGP received in CASH from " + student.getName());
        return true;
    }
}
