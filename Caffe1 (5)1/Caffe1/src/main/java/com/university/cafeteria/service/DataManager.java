package com.university.cafeteria.service;

import com.university.cafeteria.model.*;
import com.university.cafeteria.enums.*;
import java.io.*;
import java.util.*;

/**
 * DataManager handles persistent storage of all system data to text files.
 * 
 * Data files structure:
 * - users.txt: User data (students and admins)
 * - orders.txt: Order data with details
 * - menu.txt: Menu items data
 * - loyalty_offers.txt: Loyalty offers data
 * - pending_admins.txt: Pending admin registrations
 * 
 * @author System Developer
 * @version 1.0
 */
public class DataManager {
    private static final String DATA_DIR = "data";
    private static final String USERS_FILE = DATA_DIR + "/users.txt";
    private static final String ORDERS_FILE = DATA_DIR + "/orders.txt";
    private static final String MENU_FILE = DATA_DIR + "/menu.txt";
    private static final String LOYALTY_OFFERS_FILE = DATA_DIR + "/loyalty_offers.txt";
    private static final String PENDING_ADMINS_FILE = DATA_DIR + "/pending_admins.txt";
    
    /**
     * Initialize data directory and files
     */
    public static void initializeDataFiles() {
        try {
            File dataDir = new File(DATA_DIR);
            if (!dataDir.exists()) {
                dataDir.mkdirs();
            }
            
            // Create files if they don't exist
            createFileIfNotExists(USERS_FILE);
            createFileIfNotExists(ORDERS_FILE);
            createFileIfNotExists(MENU_FILE);
            createFileIfNotExists(LOYALTY_OFFERS_FILE);
            createFileIfNotExists(PENDING_ADMINS_FILE);
            
        } catch (Exception e) {
            System.err.println("Error initializing data files: " + e.getMessage());
        }
    }
    
    private static void createFileIfNotExists(String filename) throws IOException {
        File file = new File(filename);
        if (!file.exists()) {
            file.createNewFile();
        }
    }
    
    // =================== USER DATA PERSISTENCE ===================
    
    /**
     * Save all users to file
     */
    public static void saveUsers(Map<String, User> usersByUsername) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(USERS_FILE))) {
            for (User user : usersByUsername.values()) {
                if (user instanceof Student) {
                    Student student = (Student) user;
                    writer.println("STUDENT|" + student.getId() + "|" + student.getUsername() + "|" + 
                                 student.getPasswordHash() + "|" + student.getName() + "|" + 
                                 student.getLoyaltyPoints());
                } else if (user instanceof Admin) {
                    Admin admin = (Admin) user;
                    writer.println("ADMIN|" + admin.getId() + "|" + admin.getUsername() + "|" + 
                                 admin.getPasswordHash() + "|" + admin.getName() + "|" + 
                                 admin.isApproved() + "|" + admin.getRegistrationDate() + "|" + 
                                 (admin.getApprovedBy() != null ? admin.getApprovedBy() : ""));
                }
            }
        } catch (IOException e) {
            System.err.println("Error saving users: " + e.getMessage());
        }
    }
    
    /**
     * Load all users from file
     */
    public static Map<String, User> loadUsers() {
        Map<String, User> users = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(USERS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                
                String[] parts = line.split("\\|");
                if (parts[0].equals("STUDENT") && parts.length >= 6) {
                    Student student = new Student(parts[1], parts[2], parts[3], parts[4]);
                    student.addLoyaltyPoints(Integer.parseInt(parts[5]));
                    users.put(student.getUsername(), student);
                } else if (parts[0].equals("ADMIN") && parts.length >= 7) {
                    Admin admin = new Admin(parts[1], parts[2], parts[3], parts[4]);
                    if (Boolean.parseBoolean(parts[5])) {
                        admin.approve(parts.length > 7 && !parts[7].isEmpty() ? parts[7] : "SYSTEM");
                    }
                    users.put(admin.getUsername(), admin);
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading users: " + e.getMessage());
        }
        return users;
    }
    
    // =================== ORDER DATA PERSISTENCE ===================
    
    /**
     * Save single order to file
     */
    public static void saveOrder(Order order) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(ORDERS_FILE, true))) {
            StringBuilder orderLine = new StringBuilder();
            orderLine.append(order.getId()).append("|")
                    .append(order.getUserId()).append("|")
                    .append(order.getTimestamp()).append("|")
                    .append(order.getTotal()).append("|")
                    .append(order.getStatus()).append("|")
                    .append(order.isUsedPoints()).append("|")
                    .append(order.getPointsRedeemed()).append("|");
            
            // Add order items (include category)
            for (int i = 0; i < order.getItems().size(); i++) {
                MenuItem item = order.getItems().get(i);
                orderLine.append(item.getId())
                        .append(":").append(item.getName().replace("|"," "))
                        .append(":").append(item.getPrice())
                        .append(":").append(item.getCategory());
                if (i < order.getItems().size() - 1) {
                    orderLine.append(",");
                }
            }
            
            writer.println(orderLine.toString());
        } catch (IOException e) {
            System.err.println("Error saving order: " + e.getMessage());
        }
    }

    /**
     * Save all orders to file (overwrite mode)
     */
    public static void saveAllOrders(List<Order> orders) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(ORDERS_FILE))) {
            for (Order order : orders) {
                StringBuilder orderLine = new StringBuilder();
                orderLine.append(order.getId()).append("|")
                        .append(order.getUserId()).append("|")
                        .append(order.getTimestamp()).append("|")
                        .append(order.getTotal()).append("|")
                        .append(order.getStatus()).append("|")
                        .append(order.isUsedPoints()).append("|")
                        .append(order.getPointsRedeemed()).append("|");
                
                // Add order items (include category)
                for (int i = 0; i < order.getItems().size(); i++) {
                    MenuItem item = order.getItems().get(i);
                    orderLine.append(item.getId())
                            .append(":").append(item.getName().replace("|"," "))
                            .append(":").append(item.getPrice())
                            .append(":").append(item.getCategory());
                    if (i < order.getItems().size() - 1) {
                        orderLine.append(",");
                    }
                }
                
                writer.println(orderLine.toString());
            }
        } catch (IOException e) {
            System.err.println("Error saving orders: " + e.getMessage());
        }
    }
    
    /**
     * Load all orders from file
     */
    public static List<Order> loadOrders() {
        List<Order> orders = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(ORDERS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                
                String[] parts = line.split("\\|");
                if (parts.length >= 8) {
                    String orderId = parts[0];
                    String userId = parts[1];
                    long timestamp = Long.parseLong(parts[2]);
                    double total = Double.parseDouble(parts[3]);
                    OrderStatus status = OrderStatus.valueOf(parts[4]);
                    boolean usedPoints = Boolean.parseBoolean(parts[5]);
                    int pointsRedeemed = Integer.parseInt(parts[6]);
                    
                    List<MenuItem> items = new ArrayList<>();
                    if (parts.length > 7 && !parts[7].isEmpty()) {
                        String[] itemParts = parts[7].split(",");
                        for (String itemPart : itemParts) {
                            String[] itemData = itemPart.split(":");
                            // Expect id:name:price:category
                            if (itemData.length >= 4) {
                                MenuItemCategory cat;
                                try { cat = MenuItemCategory.valueOf(itemData[3]); } catch (Exception e) { cat = MenuItemCategory.MAIN_COURSE; }
                                MenuItem item = new MenuItem(itemData[0], itemData[1], "", 
                                                           Double.parseDouble(itemData[2]), cat);
                                items.add(item);
                            } else if (itemData.length >= 3) { // backward compatibility
                                MenuItem item = new MenuItem(itemData[0], itemData[1], "", 
                                                           Double.parseDouble(itemData[2]), MenuItemCategory.MAIN_COURSE);
                                items.add(item);
                            }
                        }
                    }
                    
                    Order order = new Order(orderId, userId, items, total, status, timestamp, usedPoints, pointsRedeemed);
                    orders.add(order);
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading orders: " + e.getMessage());
        }
        return orders;
    }
    
    /**
     * Update order status in file
     */
    public static void updateOrderStatus(String orderId, OrderStatus newStatus) {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(ORDERS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith(orderId + "|")) {
                    String[] parts = line.split("\\|");
                    if (parts.length >= 5) {
                        parts[4] = newStatus.toString();
                        line = String.join("|", parts);
                    }
                }
                lines.add(line);
            }
        } catch (IOException e) {
            System.err.println("Error reading orders for update: " + e.getMessage());
            return;
        }
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(ORDERS_FILE))) {
            for (String line : lines) {
                writer.println(line);
            }
        } catch (IOException e) {
            System.err.println("Error updating order status: " + e.getMessage());
        }
    }
    
    // =================== MENU DATA PERSISTENCE ===================
    
    /**
     * Save all menu items to file
     */
    public static void saveMenuItems(Map<String, MenuItem> menuItems) {
        System.out.println("DEBUG: Saving " + menuItems.size() + " menu items to file...");
        try (PrintWriter writer = new PrintWriter(new FileWriter(MENU_FILE))) {
            for (MenuItem item : menuItems.values()) {
                writer.println(item.getId() + "|" + item.getName() + "|" + item.getDescription() + "|" + 
                             item.getPrice() + "|" + item.getCategory());
            }
            System.out.println("DEBUG: Menu items saved successfully!");
        } catch (IOException e) {
            System.err.println("Error saving menu items: " + e.getMessage());
        }
    }
    
    /**
     * Load all menu items from file
     */
    public static Map<String, MenuItem> loadMenuItems() {
        Map<String, MenuItem> menuItems = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(MENU_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                
                String[] parts = line.split("\\|");
                if (parts.length >= 5) {
                    MenuItem item = new MenuItem(parts[0], parts[1], parts[2], 
                                               Double.parseDouble(parts[3]), 
                                               MenuItemCategory.valueOf(parts[4]));
                    menuItems.put(item.getId(), item);
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading menu items: " + e.getMessage());
        }
        return menuItems;
    }
    
    // =================== PENDING ADMINS PERSISTENCE ===================
    
    /**
     * Save pending admin registrations to file
     */
    public static void savePendingAdmins(List<Admin> pendingAdmins) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(PENDING_ADMINS_FILE))) {
            for (Admin admin : pendingAdmins) {
                writer.println(admin.getId() + "|" + admin.getUsername() + "|" + admin.getPasswordHash() + "|" + 
                             admin.getName() + "|" + admin.getRegistrationDate());
            }
        } catch (IOException e) {
            System.err.println("Error saving pending admins: " + e.getMessage());
        }
    }
    
    /**
     * Load pending admin registrations from file
     */
    public static List<Admin> loadPendingAdmins() {
        List<Admin> pendingAdmins = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(PENDING_ADMINS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                
                String[] parts = line.split("\\|");
                if (parts.length >= 5) {
                    Admin admin = new Admin(parts[0], parts[1], parts[2], parts[3]);
                    pendingAdmins.add(admin);
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading pending admins: " + e.getMessage());
        }
        return pendingAdmins;
    }
    
    // =================== UTILITY METHODS ===================
    
    /**
     * Save all data to files
     */
    public static void saveAllData(Map<String, User> users, Map<String, MenuItem> menuItems, 
                                  List<Admin> pendingAdmins) {
        saveUsers(users);
        saveMenuItems(menuItems);
        savePendingAdmins(pendingAdmins);
    }
    
    /**
     * Save loyalty offers to file
     */
    public static void saveLoyaltyOffers(List<LoyaltyOffer> loyaltyOffers) {
        System.out.println("DEBUG: Saving " + loyaltyOffers.size() + " loyalty offers to file...");
        try (PrintWriter writer = new PrintWriter(new FileWriter(LOYALTY_OFFERS_FILE))) {
            for (LoyaltyOffer offer : loyaltyOffers) {
                writer.println(offer.getOfferId() + "|" + offer.getMenuItemId() + "|" + 
                             offer.getPointsRequired() + "|" + offer.getOfferType() + "|" + 
                             offer.getDiscountAmount() + "|" + offer.isActive() + "|" + 
                             offer.getDescription() + "|" + offer.getCreatedBy() + "|" + 
                             offer.getCreatedTimestamp());
                System.out.println("DEBUG: Saved loyalty offer: " + offer.getDescription());
            }
            System.out.println("DEBUG: Loyalty offers saved successfully!");
        } catch (IOException e) {
            System.err.println("Error saving loyalty offers: " + e.getMessage());
        }
    }
    
    /**
     * Load loyalty offers from file
     */
    public static List<LoyaltyOffer> loadLoyaltyOffers() {
        List<LoyaltyOffer> loyaltyOffers = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(LOYALTY_OFFERS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                
                String[] parts = line.split("\\|");
                if (parts.length >= 9) {
                    String offerId = parts[0];
                    String menuItemId = parts[1];
                    int pointsRequired = Integer.parseInt(parts[2]);
                    String offerType = parts[3];
                    double discountAmount = Double.parseDouble(parts[4]);
                    boolean isActive = Boolean.parseBoolean(parts[5]);
                    String description = parts[6];
                    String createdBy = parts[7];
                    long createdTimestamp = Long.parseLong(parts[8]);
                    
                    LoyaltyOffer offer = new LoyaltyOffer(offerId, menuItemId, pointsRequired, 
                                                        offerType, discountAmount, description, createdBy);
                    offer.setActive(isActive);
                    offer.setCreatedTimestamp(createdTimestamp);
                    loyaltyOffers.add(offer);
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading loyalty offers: " + e.getMessage());
        }
        return loyaltyOffers;
    }

    /**
     * Clear all data files (for testing purposes)
     */
    public static void clearAllData() {
        try {
            new PrintWriter(USERS_FILE).close();
            new PrintWriter(ORDERS_FILE).close();
            new PrintWriter(MENU_FILE).close();
            new PrintWriter(LOYALTY_OFFERS_FILE).close();
            new PrintWriter(PENDING_ADMINS_FILE).close();
        } catch (IOException e) {
            System.err.println("Error clearing data files: " + e.getMessage());
        }
    }
}
