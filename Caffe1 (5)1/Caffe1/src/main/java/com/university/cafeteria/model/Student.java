/**
 * Student class extending User with loyalty program functionality.
 * 
 * This class demonstrates:
 * - Inheritance from User abstract class
 * - Liskov Substitution Principle (LSP) - can be used wherever User is expected
 * - Single Responsibility Principle - handles student-specific functionality
 * - Encapsulation of loyalty points management
 * 
 * Key Features:
 * - Automatic role assignment to STUDENT
 * - Loyalty points earning and redemption
 * - Points balance management with validation
 * 
 * @author System Developer
 * @version 1.0
 */
package com.university.cafeteria.model;

import com.university.cafeteria.enums.UserRole;

public class Student extends User {
    /** Current loyalty points balance for this student */
    private int loyaltyPoints;

    /**
     * Constructor for creating a new student.
     * Automatically sets role to STUDENT and initializes loyalty points to 0.
     * 
     * @param id Unique identifier for the student
     * @param username Username for authentication
     * @param passwordHash Hashed password for security
     * @param name Full name of the student
     */
    public Student(String id, String username, String passwordHash, String name) {
        super(id, username, passwordHash, name, UserRole.STUDENT);
        this.loyaltyPoints = 0; // New students start with zero points
    }

    /**
     * Gets the current loyalty points balance.
     * 
     * @return Current loyalty points
     */
    public int getLoyaltyPoints() {
        return loyaltyPoints;
    }

    /**
     * Sets the loyalty points to a specific value.
     * Used by admin functions for points adjustment.
     * 
     * @param points New loyalty points value (should be non-negative)
     */
    public void setLoyaltyPoints(int points) {
        this.loyaltyPoints = Math.max(0, points); // Ensure points are never negative
    }

    /**
     * Adds loyalty points to the current balance.
     * Used when student earns points from orders.
     * 
     * @param points Points to add (should be positive)
     */
    public void addLoyaltyPoints(int points) {
        if (points > 0) {
            this.loyaltyPoints += points;
        }
    }

    /**
     * Deducts loyalty points from the current balance.
     * Used when student redeems points for discounts.
     * Ensures balance never goes below zero.
     * 
     * @param points Points to deduct (should be positive)
     */
    public void deductLoyaltyPoints(int points) {
        if (points > 0) {
            this.loyaltyPoints = Math.max(0, this.loyaltyPoints - points);
        }
    }
}
