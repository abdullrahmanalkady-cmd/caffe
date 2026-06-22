/**
 * Admin class extending User with administrative privileges.
 * 
 * This class demonstrates:
 * - Simple inheritance from User abstract class
 * - Automatic role assignment for administrative access
 * - Liskov Substitution Principle compliance
 * - Clean separation of concerns (admin-specific logic is in service classes)
 * 
 * Admin users have access to:
 * - Menu management functions
 * - Order management and tracking
 * - User management capabilities
 * - System reports and analytics
 * - Loyalty points adjustment for students
 * 
 * @author System Developer
 * @version 1.0
 */
package com.university.cafeteria.model;

import com.university.cafeteria.enums.UserRole;

public class Admin extends User {
    /** Admin approval status - new admins need system admin approval */
    private boolean isApproved;
    
    /** Registration date for tracking purposes */
    private String registrationDate;
    
    /** System admin who approved this admin (if approved) */
    private String approvedBy;

    /**
     * Constructor for creating a new admin user.
     * Automatically sets role to ADMIN for system access control.
     * New admins start as unapproved and need system admin approval.
     * 
     * @param id Unique identifier for the admin
     * @param username Username for authentication
     * @param passwordHash Hashed password for security
     * @param name Full name of the admin
     */
    public Admin(String id, String username, String passwordHash, String name) {
        super(id, username, passwordHash, name, UserRole.ADMIN);
        this.isApproved = false; // New admins start unapproved
        this.registrationDate = java.time.LocalDateTime.now().toString();
        this.approvedBy = null;
    }
    
    /**
     * Constructor for creating system admin (pre-approved).
     * Used for the initial system admin account.
     * 
     * @param id Unique identifier for the admin
     * @param username Username for authentication
     * @param passwordHash Hashed password for security
     * @param name Full name of the admin
     * @param isSystemAdmin True if this is the system admin
     */
    public Admin(String id, String username, String passwordHash, String name, boolean isSystemAdmin) {
        super(id, username, passwordHash, name, UserRole.ADMIN);
        this.isApproved = isSystemAdmin; // System admin is pre-approved
        this.registrationDate = java.time.LocalDateTime.now().toString();
        this.approvedBy = isSystemAdmin ? "SYSTEM" : null;
    }

    // Getter methods
    public boolean isApproved() { return isApproved; }
    public String getRegistrationDate() { return registrationDate; }
    public String getApprovedBy() { return approvedBy; }

    /**
     * Approve this admin account.
     * Only system admin can approve new admin accounts.
     * 
     * @param approverUsername Username of the system admin approving this account
     */
    public void approve(String approverUsername) {
        this.isApproved = true;
        this.approvedBy = approverUsername;
    }
    
    /**
     * Revoke approval for this admin account.
     * System admin can revoke approval if needed.
     */
    public void revokeApproval() {
        this.isApproved = false;
        this.approvedBy = null;
    }
}
