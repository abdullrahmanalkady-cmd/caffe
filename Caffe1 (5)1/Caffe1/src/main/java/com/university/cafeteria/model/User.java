/**
 * Abstract base class for all system users.
 * Implements the common properties and behaviors shared by all user types.
 * 
 * This class demonstrates:
 * - Abstract class design pattern
 * - Encapsulation with protected fields and public getters
 * - Template method pattern preparation for subclasses
 * 
 * @author System Developer
 * @version 1.0
 */
package com.university.cafeteria.model;

import com.university.cafeteria.enums.UserRole;

public abstract class User {
    /** Unique identifier for the user */
    protected String id;
    
    /** Username for authentication (must be unique) */
    protected String username;
    
    /** Hashed password for security (never store plain text) */
    protected String passwordHash;
    
    /** Full name of the user for display purposes */
    protected String name;
    
    /** Role determining user permissions and access level */
    protected UserRole role;

    /**
     * Constructor for creating a new user.
     * 
     * @param id Unique identifier for the user
     * @param username Username for authentication
     * @param passwordHash Hashed password for security
     * @param name Full name of the user
     * @param role User role determining permissions
     */
    public User(String id, String username, String passwordHash, String name, UserRole role) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.name = name;
        this.role = role;
    }

    // Getter methods for accessing user properties
    public String getId() { return id; }
    public String getUsername() { return username; }
    public String getPasswordHash() { return passwordHash; }
    public String getName() { return name; }
    public UserRole getRole() { return role; }

    /**
     * Allows updating the password hash.
     * Used during registration and password changes.
     * 
     * @param passwordHash New hashed password
     */
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }
}
