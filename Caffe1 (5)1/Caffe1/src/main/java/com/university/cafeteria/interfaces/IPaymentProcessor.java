package com.university.cafeteria.interfaces;

import com.university.cafeteria.model.User;

public interface IPaymentProcessor {
    boolean processPayment(User student, double amount);
}
