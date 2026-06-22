package com.university.cafeteria.interfaces;

public interface ILoyaltyProgram {
    int calculatePoints(double orderTotal);
    boolean redeemPoints(String userId, int points);
    boolean addPoints(String userId, double orderTotal);
    int getPointsBalance(String userId);
}
