package com.university.cafeteria.service;

import com.university.cafeteria.model.User;
import com.university.cafeteria.model.Student;
import com.university.cafeteria.model.Admin;
import com.university.cafeteria.enums.UserRole;
import com.university.cafeteria.interfaces.IUserAuthentication;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.List;
import java.util.ArrayList;

public class UserManager implements IUserAuthentication {
    private Map<String, User> usersByUsername = new HashMap<>();
    private Map<String, User> usersById = new HashMap<>();
    private List<Admin> pendingAdminApprovals = new ArrayList<>();
    private static final String SYSTEM_ADMIN_USERNAME = "admin";

    public UserManager() {
        // Initialize data files
        DataManager.initializeDataFiles();
        
        // Load existing data
        loadDataFromFiles();
        
        // If no users exist, create default users
        if (usersByUsername.isEmpty()) {
            createDefaultUsers();
        }
    }
    
    /**
     * Load all data from files
     */
    private void loadDataFromFiles() {
        // Load users
        Map<String, User> loadedUsers = DataManager.loadUsers();
        usersByUsername.putAll(loadedUsers);
        for (User user : loadedUsers.values()) {
            usersById.put(user.getId(), user);
        }
        
        // Load pending admins
        pendingAdminApprovals = DataManager.loadPendingAdmins();
    }
    
    /**
     * Create default users if none exist
     */
    private void createDefaultUsers() {
        // Create system admin (pre-approved) - the main admin account
        Admin systemAdmin = new Admin(UUID.randomUUID().toString(), SYSTEM_ADMIN_USERNAME, 
                                    hashPassword("admin123"), "System Administrator", true);
        usersByUsername.put(systemAdmin.getUsername(), systemAdmin);
        usersById.put(systemAdmin.getId(), systemAdmin);
        
        // Initial dummy student
        User student = new Student(UUID.randomUUID().toString(), "student", hashPassword("student123"), "Student User");
        usersByUsername.put(student.getUsername(), student);
        usersById.put(student.getId(), student);
        
        // Save to files
        saveDataToFiles();
    }
    
    /**
     * Save all data to files
     */
    private void saveDataToFiles() {
        DataManager.saveUsers(usersByUsername);
        DataManager.savePendingAdmins(pendingAdminApprovals);
    }

    @Override
    public User login(String username, String password) {
        User user = usersByUsername.get(username);
        if (user != null && user.getPasswordHash().equals(hashPassword(password))) {
            // Check if admin is approved
            if (user instanceof Admin) {
                Admin admin = (Admin) user;
                if (!admin.isApproved() && !username.equals(SYSTEM_ADMIN_USERNAME)) {
                    return null; // Admin not approved yet
                }
            }
            return user;
        }
        return null;
    }

    @Override
    public boolean register(User user, String password) {
        if (usersByUsername.containsKey(user.getUsername())) {
            return false;
        }
        user.setPasswordHash(hashPassword(password));
        
        // If registering an admin, add to pending approvals
        if (user instanceof Admin) {
            Admin admin = (Admin) user;
            pendingAdminApprovals.add(admin);
            // Don't add to main user maps until approved
            saveDataToFiles(); // Save pending admins
            return true;
        }
        
        // Students are automatically approved
        usersByUsername.put(user.getUsername(), user);
        usersById.put(user.getId(), user);
        saveDataToFiles(); // Save to files
        return true;
    }

    /**
     * Change password for a user.
     * 
     * @param username Username of the user
     * @param currentPassword Current password for verification
     * @param newPassword New password to set
     * @return true if password changed successfully, false otherwise
     */
    public boolean changePassword(String username, String currentPassword, String newPassword) {
        User user = usersByUsername.get(username);
        if (user != null && user.getPasswordHash().equals(hashPassword(currentPassword))) {
            user.setPasswordHash(hashPassword(newPassword));
            saveDataToFiles(); // Save changes
            return true;
        }
        return false;
    }

    /**
     * Register a new admin (requires system admin approval).
     * 
     * @param username Username for the new admin
     * @param password Password for the new admin
     * @param name Full name of the new admin
     * @return true if registration request submitted successfully
     */
    public boolean registerAdmin(String username, String password, String name) {
        if (usersByUsername.containsKey(username)) {
            return false; // Username already exists
        }
        
        // Check if admin is already in pending list
        for (Admin pendingAdmin : pendingAdminApprovals) {
            if (pendingAdmin.getUsername().equals(username)) {
                return false; // Already pending approval
            }
        }
        
        // Create admin with hashed password
        Admin newAdmin = new Admin(UUID.randomUUID().toString(), username, 
                                 hashPassword(password), name);
        pendingAdminApprovals.add(newAdmin);
        saveDataToFiles(); // Save pending admins
        return true;
    }

    /**
     * Approve a pending admin registration (system admin only).
     * 
     * @param adminUsername Username of admin to approve
     * @param approverUsername Username of system admin approving
     * @return true if approved successfully
     */
    public boolean approveAdmin(String adminUsername, String approverUsername) {
        // Only system admin can approve
        User approver = usersByUsername.get(approverUsername);
        if (approver == null || !approverUsername.equals(SYSTEM_ADMIN_USERNAME)) {
            return false;
        }
        
        // Find admin in pending list
        Admin adminToApprove = null;
        for (Admin pendingAdmin : pendingAdminApprovals) {
            if (pendingAdmin.getUsername().equals(adminUsername)) {
                adminToApprove = pendingAdmin;
                break;
            }
        }
        
        if (adminToApprove != null) {
            adminToApprove.approve(approverUsername);
            pendingAdminApprovals.remove(adminToApprove);
            usersByUsername.put(adminToApprove.getUsername(), adminToApprove);
            usersById.put(adminToApprove.getId(), adminToApprove);
            saveDataToFiles(); // Save changes
            return true;
        }
        return false;
    }

    /**
     * Reject a pending admin registration (system admin only).
     * 
     * @param adminUsername Username of admin to reject
     * @param approverUsername Username of system admin rejecting
     * @return true if rejected successfully
     */
    public boolean rejectAdmin(String adminUsername, String approverUsername) {
        // Only system admin can reject
        User approver = usersByUsername.get(approverUsername);
        if (approver == null || !approverUsername.equals(SYSTEM_ADMIN_USERNAME)) {
            return false;
        }
        
        // Find and remove admin from pending list
        Admin adminToReject = null;
        for (Admin pendingAdmin : pendingAdminApprovals) {
            if (pendingAdmin.getUsername().equals(adminUsername)) {
                adminToReject = pendingAdmin;
                break;
            }
        }
        
        if (adminToReject != null) {
            pendingAdminApprovals.remove(adminToReject);
            saveDataToFiles(); // Save changes
            return true;
        }
        return false;
    }

    /**
     * Get list of pending admin approvals (system admin only).
     * 
     * @param requesterUsername Username requesting the list
     * @return List of pending admins or empty list if not authorized
     */
    public List<Admin> getPendingAdminApprovals(String requesterUsername) {
        User requester = usersByUsername.get(requesterUsername);
        if (requester != null && requesterUsername.equals(SYSTEM_ADMIN_USERNAME)) {
            return new ArrayList<>(pendingAdminApprovals);
        }
        return new ArrayList<>();
    }

    /**
     * Check if user is system admin.
     * 
     * @param username Username to check
     * @return true if user is system admin
     */
    public boolean isSystemAdmin(String username) {
        return SYSTEM_ADMIN_USERNAME.equals(username);
    }

    /**
     * Get all approved admins (system admin only).
     * 
     * @param requesterUsername Username requesting the list
     * @return List of approved admins
     */
    public List<Admin> getAllAdmins(String requesterUsername) {
        if (!isSystemAdmin(requesterUsername)) {
            return new ArrayList<>();
        }
        
        List<Admin> admins = new ArrayList<>();
        for (User user : usersByUsername.values()) {
            if (user instanceof Admin) {
                Admin admin = (Admin) user;
                if (admin.isApproved()) {
                    admins.add(admin);
                }
            }
        }
        return admins;
    }

    /**
     * Get all students.
     * 
     * @return List of all students
     */
    public List<Student> getAllStudents() {
        List<Student> students = new ArrayList<>();
        for (User user : usersByUsername.values()) {
            if (user instanceof Student) {
                students.add((Student) user);
            }
        }
        return students;
    }

    public User getUserById(String id) {
        return usersById.get(id);
    }

    public User getUserByUsername(String username) {
        return usersByUsername.get(username);
    }

    public Map<String, User> getAllUsers() {
        return usersByUsername;
    }

    public Map<String, User> getAllUsersById() {
        return usersById;
    }

    private String hashPassword(String password) {
        // Simple hash for demonstration (not secure)
        return Integer.toHexString(password.hashCode());
    }
}
