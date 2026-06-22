package com.university.cafeteria.service;

import com.university.cafeteria.interfaces.ILoyaltyProgram;
import com.university.cafeteria.model.Student;
import com.university.cafeteria.model.User;
import java.util.Map;

public class LoyaltyProgramManager implements ILoyaltyProgram {
    public static final int POINTS_PER_EGP = 10; // 1 point per 10 EGP
    public static final int POINTS_FOR_DISCOUNT = 50; // 50 points for 10 EGP discount
    public static final int DISCOUNT_AMOUNT = 10; // EGP 10 discount

    private UserManager userManager;

    public LoyaltyProgramManager(UserManager userManager) {
        this.userManager = userManager;
    }

    @Override
    public int calculatePoints(double orderTotal) {
        return (int) (orderTotal / POINTS_PER_EGP);
    }

    @Override
    public boolean addPoints(String userId, double orderTotal) {
        Map<String, User> users = userManager.getAllUsersById();
        User user = users.get(userId);
        if (user == null || !(user instanceof Student)) {
            return false;
        }
        
        Student student = (Student) user;
        int points = calculatePoints(orderTotal);
        student.addLoyaltyPoints(points);
        
        // Save the updated user data
        DataManager.saveUsers(userManager.getAllUsers());
        return true;
    }

    @Override
    public boolean redeemPoints(String userId, int points) {
        Map<String, User> users = userManager.getAllUsersById();
        User user = users.get(userId);
        if (user == null || !(user instanceof Student)) {
            return false;
        }
        
        Student student = (Student) user;
        if (student.getLoyaltyPoints() < points) {
            return false;
        }
        
        student.deductLoyaltyPoints(points);
        
        // Save the updated user data
        DataManager.saveUsers(userManager.getAllUsers());
        return true;
    }

    @Override
    public int getPointsBalance(String userId) {
        Map<String, User> users = userManager.getAllUsersById();
        User user = users.get(userId);
        if (user != null && user instanceof Student) {
            return ((Student) user).getLoyaltyPoints();
        }
        return 0;
    }
}
