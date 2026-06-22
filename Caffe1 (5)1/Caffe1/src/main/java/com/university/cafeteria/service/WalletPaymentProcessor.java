package com.university.cafeteria.service;

import java.util.HashMap;
import java.util.Map;
import com.university.cafeteria.interfaces.IPaymentProcessor;
import com.university.cafeteria.model.User;

public class WalletPaymentProcessor implements IPaymentProcessor {
    private Map<String, Double> walletBalances = new HashMap<>();

    public void addBalance(String userId, double amount) {
        walletBalances.put(userId, walletBalances.getOrDefault(userId, 0.0) + amount);
    }

    @Override
    public boolean processPayment(User student, double amount) {
        double balance = walletBalances.getOrDefault(student.getId(), 0.0);

        if (balance >= amount) {
            walletBalances.put(student.getId(), balance - amount);
            System.out.println("Payment of " + amount + " EGP processed via WALLET for " + student.getName());
            return true;
        } else {
            System.out.println("Insufficient WALLET balance for " + student.getName());
            return false;
        }
    }
}
