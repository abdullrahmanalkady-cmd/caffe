package com.university.cafeteria.service;
import com.university.cafeteria.interfaces.IPaymentProcessor;
import com.university.cafeteria.model.User;

public class CardPaymentProcessor implements IPaymentProcessor {
    @Override
    public boolean processPayment(User student, double amount) {

        System.out.println("Payment of " + amount + " EGP processed via CARD for " + student.getName());
        return true; // نفترض إن العملية نجحت
    }
}
