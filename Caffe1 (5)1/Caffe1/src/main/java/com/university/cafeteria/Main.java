/**
 * University Cafeteria Order & Loyalty System - Main Application Class
 *
 * This is a comprehensive console-based application for managing a university cafeteria system.
 * The application implements SOLID principles and provides functionality for both students and administrators.
 *
 * Key Features:
 * - Student registration and authentication
 * - Admin authentication with separate access controls
 * - Menu browsing by category or complete view
 * - Order placement with loyalty points integration
 * - Loyalty points earning and redemption system
 * - Comprehensive admin controls for menu, order, and user management
 * - Detailed reporting and analytics
 * - Real-time order status tracking
 *
 * Architecture:
 * - Uses dependency injection for service classes
 * - Implements interfaces for loose coupling
 * - Follows single responsibility principle
 * - Uses enum types for type safety
 * - Implements comprehensive error handling
 *
 * Admin Functions (as per class diagram):
 * - Complete menu management (add, edit, remove, view by category)
 * - Order management (view all, by status, update status, search)
 * - User management (view students, adjust loyalty points, view order history)
 * - Comprehensive reporting (sales, loyalty, popular items, revenue by category)
 * - System statistics and analytics
 *
 * Student Functions:
 * - Browse menu by category or view all items
 * - Place orders with multiple items selection
 * - View order history with detailed information
 * - Check and redeem loyalty points
 * - Receive notifications for order status updates
 *
 * @author System Developer
 * @version 1.0
 * @since 2025
 */
package com.university.cafeteria;

import com.university.cafeteria.enums.*;
import com.university.cafeteria.model.*;
import com.university.cafeteria.service.*;
import java.util.*;
import java.util.UUID;
import java.util.OptionalDouble;

/**
 * Main application class that orchestrates the University Cafeteria System.
 * This class contains the main method and all user interface logic.
 * It demonstrates proper separation of concerns by delegating business logic
 * to appropriate service classes while handling UI interactions here.
 */
public class Main {
    // =================== SYSTEM COMPONENTS ===================

    /** Scanner for reading user input from console */
    private static Scanner scanner = new Scanner(System.in);

    /** User management service for authentication and user operations */
    private static UserManager userManager = new UserManager();

    /** Menu management service for CRUD operations on menu items */
    private static MenuManager menuManager = new MenuManager();

    /** Map storing all students for easy access (required by LoyaltyProgramManager) */
    private static Map<String, Student> studentMap = new HashMap<>();

    /** Loyalty program service for points calculation and redemption */
    private static LoyaltyProgramManager loyaltyManager;

    /** Loyalty offer management service for point-based offers */
    private static LoyaltyOfferManager loyaltyOfferManager;

    /** Notification service for sending alerts to users */
    private static ConsoleNotificationService notificationService = new ConsoleNotificationService();

    /** Order processing service with dependency injection (DIP principle) */
    private static OrderProcessor orderProcessor;

    /** Report generation service for analytics and business intelligence */
    private static ReportGenerator reportGenerator = new ReportGenerator();

    /** Currently logged in user (null if not logged in) */
    private static User currentUser = null;

    /**
     * Main entry point of the application.
     * Initializes the system with dummy data and starts the main application loop.
     *
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        preloadStudents();
        // Initialize loyalty manager with user manager reference
        loyaltyManager = new LoyaltyProgramManager(userManager);
        // Initialize loyalty offer manager after loyalty manager is ready
        loyaltyOfferManager = new LoyaltyOfferManager(loyaltyManager, menuManager);
        // Initialize order processor with dependencies
        orderProcessor = new OrderProcessor(loyaltyManager, notificationService);

        System.out.println("=".repeat(60));
        System.out.println("    WELCOME TO UNIVERSITY CAFETERIA SYSTEM");
        System.out.println("=".repeat(60));
        System.out.println("Features:");
        System.out.println("• Student registration and ordering");
        System.out.println("• Loyalty points earning and redemption");
        System.out.println("• Menu browsing by category");
        System.out.println("• Admin management tools");
        System.out.println("• Real-time order tracking");
        System.out.println("=".repeat(60));

        // Main application loop - continues until user exits
        while (true) {
            if (currentUser == null) {
                showLoginMenu();
            } else if (currentUser.getRole() == UserRole.STUDENT) {
                showStudentMenu();
            } else if (currentUser.getRole() == UserRole.ADMIN) {
                showAdminMenu();
            }
        }
    }

    /**
     * Preloads student data from UserManager into studentMap.
     * This is required because LoyaltyProgramManager needs direct access to Student objects.
     * Demonstrates proper initialization and data synchronization.
     */
    private static void preloadStudents() {
        // Load all students from UserManager into studentMap
        Map<String, User> allUsers = userManager.getAllUsers();
        for (User user : allUsers.values()) {
            if (user instanceof Student) {
                studentMap.put(user.getId(), (Student) user);
            }
        }
    }

    /**
     * Displays the main login menu with options for registration, login, and exit
     */
    private static void showLoginMenu() {
        System.out.println("       | UNIVERSITY CAFETERIA SYSTEM |");
        System.out.println("   1. Register as Student                     ");
        System.out.println("   2. Student Login                           ");
        System.out.println("   3. Admin Login                             ");
        System.out.println("   4. Request Admin Registration              ");
        System.out.println("   5. Exit                                    ");
        System.out.print("Choose an option: ");
        String choice = scanner.nextLine();
        switch (choice) {
            case "1":
                handleRegister();
                break;
            case "2":
                handleStudentLogin();
                break;
            case "3":
                handleAdminLogin();
                break;
            case "4":
                registerAdminRequest();
                break;
            case "5":
                System.exit(0);
                break;
            default:
                System.out.println("Invalid option.");
        }
    }

    /**
     * Handles student registration process
     * Creates a new student account with unique username validation
     */
    private static void handleRegister() {
        System.out.print("Enter username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Enter password: ");
        String password = scanner.nextLine().trim();
        System.out.print("Enter your name: ");
        String name = scanner.nextLine().trim();
        String id = UUID.randomUUID().toString();
        Student student = new Student(id, username, "", name);
        if (userManager.register(student, password)) {
            studentMap.put(id, student);
            System.out.println("Registration successful. You can now login.");
        } else {
            System.out.println("Username already exists.");
        }
    }

    /**
     * Handles student login process
     */
    private static void handleStudentLogin() {
        System.out.println("\n=== Student Login ===");
        System.out.print("Enter username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Enter password: ");
        String password = scanner.nextLine().trim();
        User user = userManager.login(username, password);
        if (user != null && user.getRole() == UserRole.STUDENT) {
            currentUser = user;
            System.out.println("Login successful. Welcome, " + user.getName() + "!");
        } else {
            System.out.println("Invalid student credentials.");
        }
    }

    /**
     * Handles admin login process
     */
    private static void handleAdminLogin() {
        System.out.println("\n=== Admin Login ===");
        System.out.print("Enter admin username: ");
        String username = scanner.nextLine();
        System.out.print("Enter admin password: ");
        String password = scanner.nextLine();
        User user = userManager.login(username, password);
        if (user != null && user.getRole() == UserRole.ADMIN) {
            currentUser = user;
            System.out.println("Admin login successful. Welcome, " + user.getName() + "!");
        } else {
            System.out.println("Invalid admin credentials.");
        }
    }

    /**
     * Displays the student main menu with all available options
     */
    private static void showStudentMenu() {
        System.out.println("\n"  .repeat(8) );
        System.out.println("       |STUDENT DASHBOARD|");
        System.out.println("".repeat(53) );
        System.out.println("    1. Browse Menu by Category            ");
        System.out.println("    2. Browse All Menu Items              ");
        System.out.println("    3. Place Order                        ");
        System.out.println("    4. View My Orders                     ");
        System.out.println("    5. Check Loyalty Points               ");
        System.out.println("    6. Browse Loyalty Offers              ");
        System.out.println("    7. Redeem Loyalty Offer               ");
        System.out.println("    8. Change Password                    ");
        System.out.println("    9. Logout                             ");
        System.out.println("".repeat(4) );
        System.out.print("Choose an option: ");
        String choice = scanner.nextLine();
        switch (choice) {
            case "1":
                browseMenuByCategory();
                break;
            case "2":
                displayAllMenu();
                break;
            case "3":
                placeOrder();
                break;
            case "4":
                viewMyOrders();
                break;
            case "5":
                checkLoyaltyPoints();
                break;
            case "6":
                browseLoyaltyOffers();
                break;
            case "7":
                redeemLoyaltyOffer();
                break;
            case "8":
                changePassword();
                break;
            case "9":
                currentUser = null;
                System.out.println("Logged out successfully.");
                break;
            default:
                System.out.println("Invalid option.");
        }
    }

    /**
     * Allows students to browse menu items by category
     */
    private static void browseMenuByCategory() {
        System.out.println("\n".repeat(8));
        System.out.println("  | MENU CATEGORIES |         ");
        System.out.println("".repeat(4));
        System.out.println("   1. Main Course                      ");
        System.out.println("   2. Dessert                          ");
        System.out.println("   3. Drink                            ");
        System.out.println("   4. Snack                            ");
        System.out.println("".repeat(5) );
        System.out.print("Choose a category: ");
        String choice = scanner.nextLine();

        MenuItemCategory selectedCategory = null;
        switch (choice) {
            case "1":
                selectedCategory = MenuItemCategory.MAIN_COURSE;
                break;
            case "2":
                selectedCategory = MenuItemCategory.DESSERT;
                break;
            case "3":
                selectedCategory = MenuItemCategory.DRINK;
                break;
            case "4":
                selectedCategory = MenuItemCategory.SNACK;
                break;
            default:
                System.out.println("Invalid category selection.");
                return;
        }

        displayMenuByCategory(selectedCategory);
    }

    /**
     * Displays menu items filtered by category
     */
    private static List<MenuItem> displayMenuByCategory(MenuItemCategory category) {
        System.out.println("\n=== " + category.name().replace("_", " ") + " Menu ===");
        List<MenuItem> items = menuManager.getMenuItems().stream()
                .filter(item -> item.getCategory() == category)
                .collect(java.util.stream.Collectors.toList());
        if (items.isEmpty()) {
            System.out.println("No items available in this category.");
            return items;
        }
        for (int i = 0; i < items.size(); i++) {
            MenuItem item = items.get(i);
            System.out.printf("%d. %s - %.2f EGP\n   %s\n",
                    i + 1, item.getName(), item.getPrice(), item.getDescription());
        }
        return items;
    }

    /**
     * Displays all menu items regardless of category
     */
    private static List<MenuItem> displayAllMenu() {
        System.out.println("\n" .repeat(8) );
        System.out.println("     UNIVERSITY CAFETERIA - COMPLETE MENU ");
        System.out.println("" .repeat(8));

        List<MenuItem> items = menuManager.getMenuItems();

        // Group items by category for better organization
        Map<String, List<MenuItem>> itemsByCategory = new HashMap<>();
        for (MenuItem item : items) {
            String category = item.getCategory().name();
            List<MenuItem> list = itemsByCategory.get(category);
            if (list == null) {
                list = new ArrayList<>();
                itemsByCategory.put(category, list);
            }
            list.add(item);
        }

        int itemNumber = 1;

        // List preserving display order
        List<MenuItem> orderedDisplay = new ArrayList<>();

        // Display MAIN COURSES
        if (itemsByCategory.containsKey("MAIN_COURSE")) {
            System.out.println("\nMAIN COURSES " + "─".repeat(5) );
            for (MenuItem item : itemsByCategory.get("MAIN_COURSE")) {
                displayMenuItem(item, itemNumber++);
                orderedDisplay.add(item);
            }
            System.out.println( "".repeat(7) );
        }

        // Display SNACKS
        if (itemsByCategory.containsKey("SNACK")) {
            System.out.println("\n SNACKS & APPETIZERS " );
            for (MenuItem item : itemsByCategory.get("SNACK")) {
                displayMenuItem(item, itemNumber++);
                orderedDisplay.add(item);
            }
            System.out.println("" .repeat(8) );
        }

        // Display DRINKS
        if (itemsByCategory.containsKey("DRINK")) {
            System.out.println("\n BEVERAGES ");
            for (MenuItem item : itemsByCategory.get("DRINK")) {
                displayMenuItem(item, itemNumber++);
                orderedDisplay.add(item);
            }
            System.out.println("" + "".repeat(7) + "");
        }

        // Display DESSERTS
        if (itemsByCategory.containsKey("DESSERT")) {
            System.out.println("\n DESSERTS " + "─".repeat(6) + "");
            for (MenuItem item : itemsByCategory.get("DESSERT")) {
                displayMenuItem(item, itemNumber++);
                orderedDisplay.add(item);
            }
            System.out.println("" + "".repeat(7) + "");
        }

        System.out.println("\n".repeat(8) );
        System.out.println(" All prices are in Egyptian Pounds (EGP)                    ");
        System.out.println(" Earn loyalty points with every purchase!                   ");
        System.out.println("".repeat(8) );
        return orderedDisplay;
    }

    /**
     * Helper method to display a menu item with beautiful formatting
     */
    private static void displayMenuItem(MenuItem item, int number) {
        System.out.printf("│ %2d. %-35s %8.2f EGP │\n",
                number, item.getName(), item.getPrice());
        System.out.printf("│      %-58s │\n", item.getDescription());
        System.out.println("│" + " ".repeat(8) + "│");
    }

    /**
     * Enhanced order placement process with category selection and improved UI
     */
    private static void placeOrder() {
        List<MenuItem> selectedItems = new ArrayList<>();

        while (true) {
            System.out.println("\n".repeat(8) );
            System.out.println("           | PLACE ORDER |           ");
            System.out.println("".repeat(4) );
            System.out.println("        1. Add items by category           ");
            System.out.println("        2. Add items from full menu        ");
            System.out.println("        3. View current order              ");
            System.out.println("        4. Finalize order                  ");
            System.out.println("        5. Cancel order                    ");
            System.out.println("".repeat(4));
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine();
            switch (choice) {
                case "1":
                    addItemsByCategory(selectedItems);
                    break;
                case "2":
                    addItemsFromFullMenu(selectedItems);
                    break;
                case "3":
                    viewCurrentOrder(selectedItems);
                    break;
                case "4":
                    if (finalizeOrder(selectedItems)) return;
                    break;
                case "5":
                    System.out.println("Order cancelled.");
                    return;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    /**
     * Allows adding items to order by selecting category first
     */
    private static void addItemsByCategory(List<MenuItem> selectedItems) {
        System.out.println("".repeat(3) );
        System.out.println("      | MENU CATEGORIES |         ");
        System.out.println("".repeat(3) );
        System.out.println("    1. Main Course                  ");
        System.out.println("    2. Dessert                      ");
        System.out.println("    3. Drink                        ");
        System.out.println("    4. Snack                       ");
        System.out.println("".repeat(3) );
        System.out.print("Choose a category: ");
        String choice = scanner.nextLine();

        MenuItemCategory selectedCategory = null;
        switch (choice) {
            case "1":
                selectedCategory = MenuItemCategory.MAIN_COURSE;
                break;
            case "2":
                selectedCategory = MenuItemCategory.DESSERT;
                break;
            case "3":
                selectedCategory = MenuItemCategory.DRINK;
                break;
            case "4":
                selectedCategory = MenuItemCategory.SNACK;
                break;
            default:
                System.out.println("Invalid category selection.");
                return;
        }

        List<MenuItem> shown = displayMenuByCategory(selectedCategory);
        System.out.println("\nSelect items from the category above:");
        addItemsToOrder(selectedItems, shown);
    }

    /**
     * Allows adding items from the complete menu
     */
    private static void addItemsFromFullMenu(List<MenuItem> selectedItems) {
        List<MenuItem> shown = displayAllMenu();
        System.out.println("\nSelect items from the menu above:");
        addItemsToOrder(selectedItems, shown);
    }

    /**
     * Helper method to add items to the current order
     */
    private static void addItemsToOrder(List<MenuItem> selectedItems, List<MenuItem> availableItems) {
        if (availableItems == null || availableItems.isEmpty()) {
            System.out.println("No items available to select.");
            return;
        }
        while (true) {
            System.out.print("Enter item number to add (0 to go back): ");
            String input = scanner.nextLine();
            if (input.equals("0")) break;
            try {
                int idx = Integer.parseInt(input) - 1;
                if (idx >= 0 && idx < availableItems.size()) {
                    MenuItem selectedItem = availableItems.get(idx);
                    selectedItems.add(selectedItem);
                    System.out.println("✓ " + selectedItem.getName() + " added to order.");
                } else {
                    System.out.println("Invalid item number.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }

    /**
     * Displays the current order with total calculation
     */
    private static void viewCurrentOrder(List<MenuItem> selectedItems) {
        if (selectedItems.isEmpty()) {
            System.out.println("Your order is empty.");
            return;
        }

        System.out.println("\n=== Current Order ===");
        double total = 0;
        for (int i = 0; i < selectedItems.size(); i++) {
            MenuItem item = selectedItems.get(i);
            System.out.printf("%d. %s - %.2f EGP\n", i + 1, item.getName(), item.getPrice());
            total += item.getPrice();
        }
        System.out.printf("Total: %.2f EGP\n", total);
    }

    /**
     * Finalizes the order with loyalty points processing
     */
    private static boolean finalizeOrder(List<MenuItem> selectedItems) {
        if (selectedItems.isEmpty()) {
            System.out.println("Cannot finalize empty order.");
            return false;
        }

        double total = selectedItems.stream().mapToDouble(MenuItem::getPrice).sum();
        int discount = 0;
        int pointsToRedeem = 0;
        boolean usedPoints = false;

        if (currentUser instanceof Student) {
            int balance = loyaltyManager.getPointsBalance(currentUser.getId());
            System.out.printf("\nOrder Summary:\n");
            System.out.printf("Total: %.2f EGP\n", total);
            System.out.printf("Your loyalty points: %d\n", balance);

            // Offer loyalty point redemption for discount
            if (balance >= LoyaltyProgramManager.POINTS_FOR_DISCOUNT) {
                System.out.printf("Redeem %d points for %d EGP discount? (y/n): ",
                        LoyaltyProgramManager.POINTS_FOR_DISCOUNT,
                        LoyaltyProgramManager.DISCOUNT_AMOUNT);
                String ans = scanner.nextLine();
                if (ans.equalsIgnoreCase("y")) {
                    if (loyaltyManager.redeemPoints(currentUser.getId(), LoyaltyProgramManager.POINTS_FOR_DISCOUNT)) {
                        discount = LoyaltyProgramManager.DISCOUNT_AMOUNT;
                        pointsToRedeem = LoyaltyProgramManager.POINTS_FOR_DISCOUNT;
                        usedPoints = true;
                        System.out.println("Discount applied!");
                    } else {
                        System.out.println("ERROR: Failed to apply discount.");
                    }
                }
            }

            // Show loyalty offers for items in cart
            showAvailableLoyaltyOffers(selectedItems, balance - pointsToRedeem);
        }

        double finalTotal = Math.max(0, total - discount);

        // Calculate points to earn based on final total (after discounts)
        int pointsToEarn = loyaltyManager.calculatePoints(finalTotal);

        System.out.printf("Final total: %.2f EGP\n", finalTotal);
        System.out.printf("Points you'll earn: %d\n", pointsToEarn);
        System.out.print("Confirm order? (y/n): ");

        if (scanner.nextLine().equalsIgnoreCase("y")) {
            String orderId = UUID.randomUUID().toString();
            Order order = new Order(orderId, currentUser.getId(), new ArrayList<>(selectedItems),
                    finalTotal, OrderStatus.PENDING, System.currentTimeMillis(), usedPoints, pointsToRedeem);
            orderProcessor.placeOrder(order);

            System.out.println("Order placed successfully!");
            System.out.println("Order ID: " + orderId);
            return true;
        } else {
            System.out.println("Order cancelled.");
            return false;
        }
    }

    /**
     * Shows available loyalty offers for items in the current order
     */
    private static void showAvailableLoyaltyOffers(List<MenuItem> selectedItems, int availablePoints) {
        boolean hasOffers = false;
        System.out.println("\nAvailable Loyalty Offers for your items:");

        for (MenuItem item : selectedItems) {
            List<LoyaltyOffer> offers = loyaltyOfferManager.getOffersForMenuItem(item.getId());
            for (LoyaltyOffer offer : offers) {
                if (offer.getPointsRequired() <= availablePoints) {
                    hasOffers = true;
                    System.out.printf("   • %s (%d points)\n", offer.getDescription(), offer.getPointsRequired());
                }
            }
        }

        if (!hasOffers) {
            System.out.println("   No offers available for your current items or insufficient points.");
        } else {
            System.out.println("\nNote: You can redeem these offers from the Loyalty Program menu!");
        }
    }

    /**
     * Displays student's order history with detailed information
     */
    private static void viewMyOrders() {
        List<Order> orders = orderProcessor.getOrdersByUserId(currentUser.getId());
        if (orders.isEmpty()) {
            System.out.println("No orders found.");
            return;
        }

        System.out.println("\n=== My Order History ===");
        for (Order order : orders) {
            System.out.printf("Order ID: %s\n", order.getId());
            System.out.printf("Total: %.2f EGP\n", order.getTotal());
            System.out.printf("Status: %s\n", order.getStatus());
            System.out.printf("Used Points: %s\n", order.isUsedPoints() ? "Yes (" + order.getPointsRedeemed() + " points)" : "No");
            System.out.printf("Date: %s\n", new java.util.Date(order.getTimestamp()));
            System.out.println("Items:");
            for (MenuItem item : order.getItems()) {
                System.out.printf("  - %s (%.2f EGP)\n", item.getName(), item.getPrice());
            }
            System.out.println("─────────────────────────");
        }
    }

    /**
     * Displays current loyalty points balance and redemption options
     */
    private static void checkLoyaltyPoints() {
        if (currentUser instanceof Student) {
            int points = ((Student) currentUser).getLoyaltyPoints();
            System.out.println("\n=== Loyalty Points ===");
            System.out.println("Current Balance: " + points + " points");
            System.out.printf("Points needed for discount: %d (%.0f EGP discount)\n",
                    LoyaltyProgramManager.POINTS_FOR_DISCOUNT,
                    (double) LoyaltyProgramManager.DISCOUNT_AMOUNT);

            if (points >= LoyaltyProgramManager.POINTS_FOR_DISCOUNT) {
                System.out.println("✓ You have enough points for a discount!");
            } else {
                int needed = LoyaltyProgramManager.POINTS_FOR_DISCOUNT - points;
                System.out.printf("You need %d more points for a discount.\n", needed);
            }
        }
    }

    /**
     * Enhanced admin menu with comprehensive management options including loyalty offers
     */
    private static void showAdminMenu() {
        boolean isSystemAdmin = userManager.isSystemAdmin(currentUser.getUsername());

        System.out.println("\n=== Admin Control Panel ===");
        System.out.println("1. Menu Management");
        System.out.println("2. Order Management");
        System.out.println("3. User Management");
        System.out.println("4. Loyalty Offer Management");
        System.out.println("5. Reports & Analytics");
        System.out.println("6. System Statistics");
        System.out.println("7. Change Password");
        if (isSystemAdmin) {
            System.out.println("8. Admin Management (System Admin Only)");
            System.out.println("9. Logout");
        } else {
            System.out.println("8. Logout");
        }
        System.out.print("Choose an option: ");
        String choice = scanner.nextLine();
        switch (choice) {
            case "1":
                showMenuManagementMenu();
                break;
            case "2":
                showOrderManagementMenu();
                break;
            case "3":
                showUserManagementMenu();
                break;
            case "4":
                showLoyaltyOfferManagementMenu();
                break;
            case "5":
                showReportsMenu();
                break;
            case "6":
                showSystemStatistics();
                break;
            case "7":
                changePassword();
                break;
            case "8":
                if (isSystemAdmin) {
                    showAdminManagementMenu();
                } else {
                    currentUser = null;
                    System.out.println("Admin logged out successfully.");
                }
                break;
            case "9":
                if (isSystemAdmin) {
                    currentUser = null;
                    System.out.println("System Admin logged out successfully.");
                }
                break;
            default:
                System.out.println("Invalid option.");
        }
    }

    /**
     * Menu management submenu for admins
     */
    private static void showMenuManagementMenu() {
        while (true) {
            System.out.println("\n=== Menu Management ===");
            System.out.println("1. View All Menu Items");
            System.out.println("2. Add Menu Item");
            System.out.println("3. Edit Menu Item");
            System.out.println("4. Remove Menu Item");
            System.out.println("5. View Items by Category");
            System.out.println("6. Back to Admin Menu");
            System.out.print("Choose an option: ");
            String choice = scanner.nextLine();
            switch (choice) {
                case "1":
                    viewAllMenuItemsAdmin();
                    break;
                case "2":
                    addMenuItem();
                    break;
                case "3":
                    editMenuItem();
                    break;
                case "4":
                    removeMenuItem();
                    break;
                case "5":
                    viewMenuItemsByCategory();
                    break;
                case "6":
                    return;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    /**
     * Order management submenu for admins
     */
    private static void showOrderManagementMenu() {
        while (true) {
            System.out.println("\n=== Order Management ===");
            System.out.println("1. View All Orders");
            System.out.println("2. View Pending Orders");
            System.out.println("3. View Orders by Status");
            System.out.println("4. Update Order Status");
            System.out.println("5. Search Order by ID");
            System.out.println("6. Back to Admin Menu");
            System.out.print("Choose an option: ");
            String choice = scanner.nextLine();
            switch (choice) {
                case "1":
                    viewAllOrders();
                    break;
                case "2":
                    viewPendingOrders();
                    break;
                case "3":
                    viewOrdersByStatus();
                    break;
                case "4":
                    updateOrderStatus();
                    break;
                case "5":
                    searchOrderById();
                    break;
                case "6":
                    return;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    /**
     * User management submenu for admins
     */
    private static void showUserManagementMenu() {
        while (true) {
            System.out.println("\n=== User Management ===");
            System.out.println("1. View All Students");
            System.out.println("2. Search Student by Username");
            System.out.println("3. View Student Loyalty Points");
            System.out.println("4. Adjust Student Loyalty Points");
            System.out.println("5. View Student Order History");
            System.out.println("6. Back to Admin Menu");
            System.out.print("Choose an option: ");
            String choice = scanner.nextLine();
            switch (choice) {
                case "1":
                    viewAllStudents();
                    break;
                case "2":
                    searchStudentByUsername();
                    break;
                case "3":
                    viewStudentLoyaltyPoints();
                    break;
                case "4":
                    adjustStudentLoyaltyPoints();
                    break;
                case "5":
                    viewStudentOrderHistory();
                    break;
                case "6":
                    return;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    /**
     * Reports and analytics submenu for admins
     */
    private static void showReportsMenu() {
        while (true) {
            System.out.println("\n=== Reports & Analytics ===");
            System.out.println("1. Daily Sales Report");
            System.out.println("2. Weekly Sales Report");
            System.out.println("3. Loyalty Points Redemption Report");
            System.out.println("4. Popular Items Report");
            System.out.println("5. Revenue by Category");
            System.out.println("6. Back to Admin Menu");
            System.out.print("Choose an option: ");
            String choice = scanner.nextLine();
            switch (choice) {
                case "1":
                    generateDailySalesReport();
                    break;
                case "2":
                    generateWeeklySalesReport();
                    break;
                case "3":
                    generateLoyaltyRedemptionReport();
                    break;
                case "4":
                    generatePopularItemsReport();
                    break;
                case "5":
                    generateRevenueByCategoryReport();
                    break;
                case "6":
                    return;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    // =================== ADMIN MENU MANAGEMENT FUNCTIONS ===================

    /**
     * Admin view of all menu items with management options
     */
    private static void viewAllMenuItemsAdmin() {
        System.out.println("\n=== All Menu Items (Admin View) ===");
        List<MenuItem> items = menuManager.getMenuItems();
        if (items.isEmpty()) {
            System.out.println("No menu items available.");
            return;
        }

        for (int i = 0; i < items.size(); i++) {
            MenuItem item = items.get(i);
            System.out.printf("%d. [ID: %s] %s - %.2f EGP [%s]\n   Description: %s\n",
                    i + 1, item.getId(), item.getName(), item.getPrice(),
                    item.getCategory().name().replace("_", " "), item.getDescription());
        }
    }

    /**
     * View menu items filtered by category for admin
     */
    private static void viewMenuItemsByCategory() {
        System.out.println("\n=== View Items by Category ===");
        System.out.println("1. Main Course");
        System.out.println("2. Dessert");
        System.out.println("3. Drink");
        System.out.println("4. Snack");
        System.out.print("Choose category to view: ");

        String choice = scanner.nextLine();
        MenuItemCategory selectedCategory = null;

        switch (choice) {
            case "1": selectedCategory = MenuItemCategory.MAIN_COURSE; break;
            case "2": selectedCategory = MenuItemCategory.DESSERT; break;
            case "3": selectedCategory = MenuItemCategory.DRINK; break;
            case "4": selectedCategory = MenuItemCategory.SNACK; break;
            default:
                System.out.println("Invalid category selection.");
                return;
        }

        final MenuItemCategory category = selectedCategory;
        System.out.println("\n=== " + category.name().replace("_", " ") + " Items ===");
        List<MenuItem> items = menuManager.getMenuItems().stream()
                .filter(item -> item.getCategory() == category)
                .collect(java.util.stream.Collectors.toList());

        if (items.isEmpty()) {
            System.out.println("No items in this category.");
        } else {
            for (MenuItem item : items) {
                System.out.printf("• %s - %.2f EGP\n  %s\n",
                        item.getName(), item.getPrice(), item.getDescription());
            }
        }
    }

    /**
     * Enhanced add menu item with validation
     */
    private static void addMenuItem() {
        System.out.println("\n=== Add New Menu Item ===");
        try {
            System.out.print("Enter item name: ");
            String name = scanner.nextLine().trim();
            if (name.isEmpty()) {
                System.out.println("Item name cannot be empty.");
                return;
            }

            System.out.print("Enter description: ");
            String desc = scanner.nextLine().trim();

            System.out.print("Enter price (EGP): ");
            double price = Double.parseDouble(scanner.nextLine());
            if (price <= 0) {
                System.out.println("Price must be positive.");
                return;
            }

            System.out.println("Select category:");
            System.out.println("1. Main Course");
            System.out.println("2. Dessert");
            System.out.println("3. Drink");
            System.out.println("4. Snack");
            System.out.print("Choose category (1-4): ");

            String catChoice = scanner.nextLine();
            MenuItemCategory category;
            switch (catChoice) {
                case "1": category = MenuItemCategory.MAIN_COURSE; break;
                case "2": category = MenuItemCategory.DESSERT; break;
                case "3": category = MenuItemCategory.DRINK; break;
                case "4": category = MenuItemCategory.SNACK; break;
                default:
                    System.out.println("Invalid category selection.");
                    return;
            }

            MenuItem item = new MenuItem(UUID.randomUUID().toString(), name, desc, price, category);
            if (menuManager.addMenuItem(item)) {
                System.out.println("✓ Menu item added successfully!");
                System.out.printf("Added: %s (%.2f EGP) in %s category\n",
                        name, price, category.name().replace("_", " "));
            } else {
                System.out.println("✗ Failed to add menu item.");
            }
        } catch (NumberFormatException e) {
            System.out.println("✗ Invalid price format. Please enter a valid number.");
        }
    }

    /**
     * Enhanced edit menu item with better user experience
     */
    private static void editMenuItem() {
        List<MenuItem> items = menuManager.getMenuItems();
        if (items.isEmpty()) {
            System.out.println("No menu items to edit.");
            return;
        }

        viewAllMenuItemsAdmin();
        System.out.print("Enter item number to edit (1-" + items.size() + "): ");

        try {
            int idx = Integer.parseInt(scanner.nextLine()) - 1;
            if (idx < 0 || idx >= items.size()) {
                System.out.println("Invalid item number.");
                return;
            }

            MenuItem item = items.get(idx);
            System.out.println("\n=== Editing: " + item.getName() + " ===");
            System.out.println("Leave blank to keep current value.");

            System.out.printf("Current name: %s\nNew name: ", item.getName());
            String name = scanner.nextLine().trim();
            if (!name.isEmpty()) item.setName(name);

            System.out.printf("Current description: %s\nNew description: ", item.getDescription());
            String desc = scanner.nextLine().trim();
            if (!desc.isEmpty()) item.setDescription(desc);

            System.out.printf("Current price: %.2f EGP\nNew price: ", item.getPrice());
            String priceStr = scanner.nextLine().trim();
            if (!priceStr.isEmpty()) {
                try {
                    double price = Double.parseDouble(priceStr);
                    if (price > 0) {
                        item.setPrice(price);
                    } else {
                        System.out.println("Price must be positive. Keeping current price.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid price format. Keeping current price.");
                }
            }

            System.out.printf("Current category: %s\n", item.getCategory().name().replace("_", " "));
            System.out.println("1. Main Course  2. Dessert  3. Drink  4. Snack");
            System.out.print("New category (1-4, blank to keep): ");
            String catStr = scanner.nextLine().trim();
            if (!catStr.isEmpty()) {
                try {
                    switch (catStr) {
                        case "1": item.setCategory(MenuItemCategory.MAIN_COURSE); break;
                        case "2": item.setCategory(MenuItemCategory.DESSERT); break;
                        case "3": item.setCategory(MenuItemCategory.DRINK); break;
                        case "4": item.setCategory(MenuItemCategory.SNACK); break;
                        default: System.out.println("Invalid category. Keeping current category.");
                    }
                } catch (Exception e) {
                    System.out.println("Error updating category. Keeping current category.");
                }
            }

            if (menuManager.editMenuItem(item)) {
                System.out.println("✓ Menu item updated successfully!");
            } else {
                System.out.println("✗ Failed to update menu item.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a valid number.");
        }
    }

    /**
     * Enhanced remove menu item with confirmation
     */
    private static void removeMenuItem() {
        List<MenuItem> items = menuManager.getMenuItems();
        if (items.isEmpty()) {
            System.out.println("No menu items to remove.");
            return;
        }

        viewAllMenuItemsAdmin();
        System.out.print("Enter item number to remove (1-" + items.size() + "): ");

        try {
            int idx = Integer.parseInt(scanner.nextLine()) - 1;
            if (idx < 0 || idx >= items.size()) {
                System.out.println("Invalid item number.");
                return;
            }

            MenuItem item = items.get(idx);
            System.out.printf("Are you sure you want to remove '%s'? (y/n): ", item.getName());
            String confirmation = scanner.nextLine().trim();

            if (confirmation.equalsIgnoreCase("y")) {
                if (menuManager.removeMenuItem(item.getId())) {
                    System.out.println("✓ Menu item removed successfully!");
                } else {
                    System.out.println("✗ Failed to remove menu item.");
                }
            } else {
                System.out.println("Removal cancelled.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a valid number.");
        }
    }

    // =================== ADMIN ORDER MANAGEMENT FUNCTIONS ===================

    /**
     * View all orders in the system (admin)
     */
    private static void viewAllOrders() {
        List<Order> orders = orderProcessor.getAllOrders();
        if (orders.isEmpty()) {
            System.out.println("No orders in the system.");
            return;
        }

        System.out.println("\n=== All Orders (Admin View) ===");
        for (Order order : orders) {
            User user = userManager.getUserById(order.getUserId());
            String userName = user != null ? user.getName() : "Unknown User";
            System.out.printf("Order ID: %s | User: %s | Total: %.2f EGP | Status: %s\n",
                    order.getId(), userName, order.getTotal(), order.getStatus());
            System.out.printf("Date: %s | Used Points: %s\n",
                    new java.util.Date(order.getTimestamp()),
                    order.isUsedPoints() ? "Yes (" + order.getPointsRedeemed() + ")" : "No");
            System.out.println("─────────────────────────");
        }
    }

    /**
     * View orders filtered by status
     */
    private static void viewOrdersByStatus() {
        System.out.println("\n=== View Orders by Status ===");
        System.out.println("1. Pending");
        System.out.println("2. Preparing");
        System.out.println("3. Ready for Pickup");
        System.out.println("4. Completed");
        System.out.print("Choose status: ");

        String choice = scanner.nextLine();
        String status = null;

        switch (choice) {
            case "1": status = "PENDING"; break;
            case "2": status = "PREPARING"; break;
            case "3": status = "READY_FOR_PICKUP"; break;
            case "4": status = "COMPLETED"; break;
            default:
                System.out.println("Invalid status selection.");
                return;
        }

        List<Order> orders = orderProcessor.getOrdersByStatus(status);
        if (orders.isEmpty()) {
            System.out.println("No orders with status: " + status);
            return;
        }

        System.out.println("\n=== " + status.replace("_", " ") + " Orders ===");
        for (Order order : orders) {
            User user = userManager.getUserById(order.getUserId());
            String userName = user != null ? user.getName() : "Unknown User";
            System.out.printf("Order ID: %s | User: %s | Total: %.2f EGP\n",
                    order.getId(), userName, order.getTotal());
        }
    }

    /**
     * Update order status (enhanced)
     */
    private static void updateOrderStatus() {
        System.out.print("Enter Order ID: ");
        String orderId = scanner.nextLine().trim();

        Order order = orderProcessor.getOrderById(orderId);
        if (order == null) {
            System.out.println("Order not found.");
            return;
        }

        User user = userManager.getUserById(order.getUserId());
        String userName = user != null ? user.getName() : "Unknown User";

        System.out.printf("\nOrder Details:\n");
        System.out.printf("ID: %s\nUser: %s\nTotal: %.2f EGP\nCurrent Status: %s\n",
                order.getId(), userName, order.getTotal(), order.getStatus());

        System.out.println("\nSelect new status:");
        System.out.println("1. Preparing");
        System.out.println("2. Ready for Pickup");
        System.out.println("3. Completed");
        System.out.print("Choose new status: ");

        String choice = scanner.nextLine();
        String newStatus = null;

        switch (choice) {
            case "1": newStatus = "PREPARING"; break;
            case "2": newStatus = "READY_FOR_PICKUP"; break;
            case "3": newStatus = "COMPLETED"; break;
            default:
                System.out.println("Invalid status selection.");
                return;
        }

        if (orderProcessor.updateOrderStatus(orderId, newStatus)) {
            System.out.println("✓ Order status updated successfully!");
        } else {
            System.out.println("✗ Failed to update order status.");
        }
    }

    /**
     * Search for a specific order by ID
     */
    private static void searchOrderById() {
        System.out.print("Enter Order ID: ");
        String orderId = scanner.nextLine().trim();

        Order order = orderProcessor.getOrderById(orderId);
        if (order == null) {
            System.out.println("Order not found.");
            return;
        }

        User user = userManager.getUserById(order.getUserId());
        String userName = user != null ? user.getName() : "Unknown User";

        System.out.println("\n=== Order Details ===");
        System.out.printf("Order ID: %s\n", order.getId());
        System.out.printf("Customer: %s (ID: %s)\n", userName, order.getUserId());
        System.out.printf("Total: %.2f EGP\n", order.getTotal());
        System.out.printf("Status: %s\n", order.getStatus());
        System.out.printf("Date: %s\n", new java.util.Date(order.getTimestamp()));
        System.out.printf("Used Points: %s\n", order.isUsedPoints() ? "Yes (" + order.getPointsRedeemed() + " points)" : "No");

        System.out.println("Items:");
        for (MenuItem item : order.getItems()) {
            System.out.printf("  - %s (%.2f EGP)\n", item.getName(), item.getPrice());
        }
    }

    // =================== ADMIN USER MANAGEMENT FUNCTIONS ===================

    /**
     * View all students in the system
     */
    private static void viewAllStudents() {
        Map<String, User> allUsers = userManager.getAllUsers();
        List<Student> students = new ArrayList<>();

        // Collect all students from the user manager
        for (User user : allUsers.values()) {
            if (user instanceof Student) {
                students.add((Student) user);
            }
        }

        if (students.isEmpty()) {
            System.out.println("No students registered.");
            return;
        }

        System.out.println("\n=== All Students ===");
        for (Student student : students) {
            System.out.printf("ID: %s | Username: %s | Name: %s | Loyalty Points: %d\n",
                    student.getId(), student.getUsername(), student.getName(), student.getLoyaltyPoints());
        }
    }

    /**
     * Search for a student by username
     */
    private static void searchStudentByUsername() {
        System.out.print("Enter student username: ");
        String username = scanner.nextLine().trim();

        User user = userManager.getUserByUsername(username);
        if (user == null || !(user instanceof Student)) {
            System.out.println("Student not found.");
            return;
        }

        Student student = (Student) user;
        System.out.println("\n=== Student Details ===");
        System.out.printf("ID: %s\n", student.getId());
        System.out.printf("Username: %s\n", student.getUsername());
        System.out.printf("Name: %s\n", student.getName());
        System.out.printf("Loyalty Points: %d\n", student.getLoyaltyPoints());

        // Show recent orders
        List<Order> orders = orderProcessor.getOrdersByUserId(student.getId());
        System.out.printf("Total Orders: %d\n", orders.size());
    }

    /**
     * View loyalty points for a specific student
     */
    private static void viewStudentLoyaltyPoints() {
        System.out.print("Enter student username: ");
        String username = scanner.nextLine().trim();

        User user = userManager.getUserByUsername(username);
        if (user == null || !(user instanceof Student)) {
            System.out.println("Student not found.");
            return;
        }

        Student student = (Student) user;
        System.out.printf("\nLoyalty Points for %s: %d points\n",
                student.getName(), student.getLoyaltyPoints());
    }

    /**
     * Adjust student loyalty points (admin function)
     */
    private static void adjustStudentLoyaltyPoints() {
        System.out.print("Enter student username: ");
        String username = scanner.nextLine().trim();

        User user = userManager.getUserByUsername(username);
        if (user == null || !(user instanceof Student)) {
            System.out.println("Student not found.");
            return;
        }

        Student student = (Student) user;
        System.out.printf("Current points for %s: %d\n", student.getName(), student.getLoyaltyPoints());
        System.out.print("Enter new points value: ");

        try {
            int newPoints = Integer.parseInt(scanner.nextLine());
            if (newPoints < 0) {
                System.out.println("Points cannot be negative.");
                return;
            }

            student.setLoyaltyPoints(newPoints);
            System.out.printf("✓ Loyalty points updated to %d for %s\n", newPoints, student.getName());
        } catch (NumberFormatException e) {
            System.out.println("Invalid number format.");
        }
    }

    /**
     * View order history for a specific student
     */
    private static void viewStudentOrderHistory() {
        System.out.print("Enter student username: ");
        String username = scanner.nextLine().trim();

        User user = userManager.getUserByUsername(username);
        if (user == null || !(user instanceof Student)) {
            System.out.println("Student not found.");
            return;
        }

        List<Order> orders = orderProcessor.getOrdersByUserId(user.getId());
        if (orders.isEmpty()) {
            System.out.printf("No orders found for %s.\n", user.getName());
            return;
        }

        System.out.printf("\n=== Order History for %s ===\n", user.getName());
        for (Order order : orders) {
            System.out.printf("Order ID: %s | Total: %.2f EGP | Status: %s | Date: %s\n",
                    order.getId(), order.getTotal(), order.getStatus(),
                    new java.util.Date(order.getTimestamp()));
        }
    }

    /**
     * Enhanced view pending orders function
     */
    private static void viewPendingOrders() {
        List<Order> pending = orderProcessor.getOrdersByStatus(OrderStatus.PENDING.name());
        if (pending.isEmpty()) {
            System.out.println("No pending orders.");
            return;
        }

        System.out.println("\n=== Pending Orders ===");
        for (Order order : pending) {
            User user = userManager.getUserById(order.getUserId());
            String userName = user != null ? user.getName() : "Unknown User";
            System.out.printf("Order ID: %s | Customer: %s | Total: %.2f EGP | Date: %s\n",
                    order.getId(), userName, order.getTotal(),
                    new java.util.Date(order.getTimestamp()));
            System.out.println("Items: " + order.getItems().size() + " items");
            System.out.println("─────────────────────────");
        }
    }

    // =================== ADMIN REPORTING FUNCTIONS ===================

    /**
     * Generate daily sales report
     */
    private static void generateDailySalesReport() {
        List<Order> allOrders = orderProcessor.getAllOrders();
        reportGenerator.printDailySales(allOrders);
    }

    /**
     * Generate weekly sales report
     */
    private static void generateWeeklySalesReport() {
        List<Order> allOrders = orderProcessor.getAllOrders();
        reportGenerator.printWeeklySales(allOrders);
    }

    /**
     * Generate loyalty redemption report
     */
    private static void generateLoyaltyRedemptionReport() {
        List<Order> allOrders = orderProcessor.getAllOrders();
        reportGenerator.printLoyaltyRedemptions(allOrders);
    }

    /**
     * Generate popular items report
     */
    private static void generatePopularItemsReport() {
        List<Order> allOrders = orderProcessor.getAllOrders();
        Map<String, Integer> itemCounts = new HashMap<>();

        System.out.println("\n=== Popular Items Report ===");

        // Count occurrences of each item
        for (Order order : allOrders) {
            for (MenuItem item : order.getItems()) {
                itemCounts.put(item.getName(), itemCounts.getOrDefault(item.getName(), 0) + 1);
            }
        }

        if (itemCounts.isEmpty()) {
            System.out.println("No items sold yet.");
            return;
        }

        // Sort by popularity
        itemCounts.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .forEach(entry -> System.out.printf("%s: %d orders\n", entry.getKey(), entry.getValue()));
    }

    /**
     * Generate revenue by category report
     */
    private static void generateRevenueByCategoryReport() {
        List<Order> allOrders = orderProcessor.getAllOrders();
        Map<MenuItemCategory, Double> categoryRevenue = new HashMap<>();

        System.out.println("\n=== Revenue by Category Report ===");

        // Calculate revenue by category
        for (Order order : allOrders) {
            if (order.getStatus() == OrderStatus.COMPLETED) {
                for (MenuItem item : order.getItems()) {
                    MenuItemCategory category = item.getCategory();
                    categoryRevenue.put(category,
                            categoryRevenue.getOrDefault(category, 0.0) + item.getPrice());
                }
            }
        }

        if (categoryRevenue.isEmpty()) {
            System.out.println("No completed orders yet.");
            return;
        }

        double totalRevenue = categoryRevenue.values().stream().mapToDouble(Double::doubleValue).sum();

        for (Map.Entry<MenuItemCategory, Double> entry : categoryRevenue.entrySet()) {
            double percentage = (entry.getValue() / totalRevenue) * 100;
            System.out.printf("%s: %.2f EGP (%.1f%%)\n",
                    entry.getKey().name().replace("_", " "),
                    entry.getValue(), percentage);
        }
        System.out.printf("Total Revenue: %.2f EGP\n", totalRevenue);
    }

    // =================== STUDENT LOYALTY OFFER FUNCTIONS ===================

    /**
     * Allows students to browse available loyalty offers
     */
    private static void browseLoyaltyOffers() {
        if (!(currentUser instanceof Student)) {
            System.out.println("Only students can browse loyalty offers.");
            return;
        }

        Student student = (Student) currentUser;
        int studentPoints = student.getLoyaltyPoints();

        System.out.println("\n=== Available Loyalty Offers ===");
        System.out.printf("Your Current Points: %d\n", studentPoints);
        System.out.println("─────────────────────────────────");

        List<com.university.cafeteria.model.LoyaltyOffer> activeOffers = loyaltyOfferManager.getActiveOffers();
        if (activeOffers.isEmpty()) {
            System.out.println("No loyalty offers available at this time.");
            return;
        }

        for (int i = 0; i < activeOffers.size(); i++) {
            com.university.cafeteria.model.LoyaltyOffer offer = activeOffers.get(i);
            MenuItem menuItem = loyaltyOfferManager.getMenuItemForOffer(offer);
            String itemName = menuItem != null ? menuItem.getName() : "Unknown Item";

            System.out.printf("%d. %s\n", i + 1, offer.getDescription());
            System.out.printf("   Item: %s\n", itemName);
            System.out.printf("   Points Required: %d\n", offer.getPointsRequired());

            if (offer.isFreeItemOffer()) {
                System.out.printf("   Type: FREE ITEM\n");
            } else {
                System.out.printf("   Type: DISCOUNT (%.2f EGP off)\n", offer.getDiscountAmount());
                System.out.printf("   Final Price: %.2f EGP\n", loyaltyOfferManager.calculateOfferPrice(offer));
            }

            if (studentPoints >= offer.getPointsRequired()) {
                System.out.printf("   Status: ✓ AVAILABLE\n");
            } else {
                System.out.printf("   Status: ✗ Need %d more points\n",
                        offer.getPointsRequired() - studentPoints);
            }
            System.out.println("─────────────────────────────────");
        }

        // Show affordable offers specifically
        List<com.university.cafeteria.model.LoyaltyOffer> affordableOffers = loyaltyOfferManager.getAffordableOffers(studentPoints);
        if (!affordableOffers.isEmpty()) {
            System.out.printf("\n✓ You can redeem %d offer(s) with your current points!\n", affordableOffers.size());
        }
    }

    /**
     * Allows students to redeem loyalty offers
     */
    private static void redeemLoyaltyOffer() {
        if (!(currentUser instanceof Student)) {
            System.out.println("Only students can redeem loyalty offers.");
            return;
        }

        Student student = (Student) currentUser;
        int studentPoints = student.getLoyaltyPoints();

        List<com.university.cafeteria.model.LoyaltyOffer> affordableOffers = loyaltyOfferManager.getAffordableOffers(studentPoints);
        if (affordableOffers.isEmpty()) {
            System.out.println("You don't have enough points for any current offers.");
            System.out.printf("Your current points: %d\n", studentPoints);
            return;
        }

        System.out.println("\n=== Redeem Loyalty Offer ===");
        System.out.printf("Your Current Points: %d\n", studentPoints);
        System.out.println("Available Offers:");

        for (int i = 0; i < affordableOffers.size(); i++) {
            com.university.cafeteria.model.LoyaltyOffer offer = affordableOffers.get(i);
            MenuItem menuItem = loyaltyOfferManager.getMenuItemForOffer(offer);
            String itemName = menuItem != null ? menuItem.getName() : "Unknown Item";

            System.out.printf("%d. %s (%d points)\n", i + 1, offer.getDescription(), offer.getPointsRequired());
            System.out.printf("   Item: %s\n", itemName);
            if (offer.isFreeItemOffer()) {
                System.out.printf("   You'll get: FREE %s\n", itemName);
            } else {
                System.out.printf("   You'll get: %.2f EGP discount on %s\n",
                        offer.getDiscountAmount(), itemName);
            }
        }

        System.out.print("Enter offer number to redeem (0 to cancel): ");
        String input = scanner.nextLine().trim();

        if (input.equals("0")) {
            System.out.println("Redemption cancelled.");
            return;
        }

        try {
            int choice = Integer.parseInt(input) - 1;
            if (choice < 0 || choice >= affordableOffers.size()) {
                System.out.println("Invalid offer selection.");
                return;
            }

            com.university.cafeteria.model.LoyaltyOffer selectedOffer = affordableOffers.get(choice);
            MenuItem menuItem = loyaltyOfferManager.getMenuItemForOffer(selectedOffer);

            System.out.printf("\nRedemption Summary:\n");
            System.out.printf("Offer: %s\n", selectedOffer.getDescription());
            System.out.printf("Points Required: %d\n", selectedOffer.getPointsRequired());
            System.out.printf("Points After Redemption: %d\n",
                    studentPoints - selectedOffer.getPointsRequired());

            System.out.print("Confirm redemption? (y/n): ");
            String confirmation = scanner.nextLine().trim();

            if (confirmation.equalsIgnoreCase("y")) {
                if (loyaltyOfferManager.redeemOffer(selectedOffer.getOfferId(), student.getId())) {
                    // Create an order record for the redeemed offer
                    String orderId = UUID.randomUUID().toString();
                    List<MenuItem> orderItems = new ArrayList<>();
                    orderItems.add(menuItem);

                    // Determine order total based on offer type
                    double orderTotal = 0.0;
                    if (selectedOffer.isFreeItemOffer()) {
                        orderTotal = 0.0; // Free item
                    } else {
                        // Discount offer - record the discounted price
                        orderTotal = Math.max(0, menuItem.getPrice() - selectedOffer.getDiscountAmount());
                    }

                    // Create order record for loyalty offer redemption
                    Order loyaltyOrder = new Order(
                            orderId,
                            student.getId(),
                            orderItems,
                            orderTotal,
                            OrderStatus.READY_FOR_PICKUP, // Loyalty offers are immediately available
                            System.currentTimeMillis(),
                            true, // Used points
                            selectedOffer.getPointsRequired()
                    );

                    // Process the order through the system
                    orderProcessor.placeOrder(loyaltyOrder);

                    System.out.println("SUCCESS: Offer redeemed successfully!");
                    System.out.println("Order ID: " + orderId);

                    if (selectedOffer.isFreeItemOffer()) {
                        System.out.printf("You can now claim your free %s at the counter.\n",
                                menuItem != null ? menuItem.getName() : "item");
                    } else {
                        System.out.printf("You can now purchase %s with %.2f EGP discount.\n",
                                menuItem != null ? menuItem.getName() : "item",
                                selectedOffer.getDiscountAmount());
                    }

                    // Points already deducted by loyaltyOfferManager.redeemOffer()
                    // Update local student object to reflect the new balance
                    student.setLoyaltyPoints(loyaltyManager.getPointsBalance(student.getId()));
                    System.out.printf("Remaining Points: %d\n", student.getLoyaltyPoints());
                } else {
                    System.out.println("ERROR: Failed to redeem offer. Please try again.");
                }
            } else {
                System.out.println("Redemption cancelled.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
        }
    }

    // =================== ADMIN LOYALTY OFFER MANAGEMENT FUNCTIONS ===================

    /**
     * Loyalty offer management submenu for admins
     */
    private static void showLoyaltyOfferManagementMenu() {
        while (true) {
            System.out.println("\n=== Loyalty Offer Management ===");
            System.out.println("1. View All Offers");
            System.out.println("2. Create New Offer");
            System.out.println("3. Edit Existing Offer");
            System.out.println("4. Activate/Deactivate Offer");
            System.out.println("5. Remove Offer");
            System.out.println("6. View Offer Analytics");
            System.out.println("7. Back to Admin Menu");
            System.out.print("Choose an option: ");
            String choice = scanner.nextLine();
            switch (choice) {
                case "1":
                    viewAllLoyaltyOffers();
                    break;
                case "2":
                    createNewLoyaltyOffer();
                    break;
                case "3":
                    editLoyaltyOffer();
                    break;
                case "4":
                    toggleOfferStatus();
                    break;
                case "5":
                    removeLoyaltyOffer();
                    break;
                case "6":
                    viewOfferAnalytics();
                    break;
                case "7":
                    return;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    /**
     * Show system statistics
     */
    private static void showSystemStatistics() {
        System.out.println("\n=== System Statistics ===");

        // User statistics
        int totalStudents = studentMap.size();
        System.out.printf("Total Students: %d\n", totalStudents);

        // Menu statistics
        List<MenuItem> menuItems = menuManager.getMenuItems();
        System.out.printf("Total Menu Items: %d\n", menuItems.size());

        Map<MenuItemCategory, Long> categoryCount = menuItems.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        MenuItem::getCategory,
                        java.util.stream.Collectors.counting()));

        System.out.println("Items by Category:");
        for (Map.Entry<MenuItemCategory, Long> entry : categoryCount.entrySet()) {
            System.out.printf("  %s: %d items\n",
                    entry.getKey().name().replace("_", " "), entry.getValue());
        }

        // Order statistics
        List<Order> allOrders = orderProcessor.getAllOrders();
        System.out.printf("Total Orders: %d\n", allOrders.size());

        Map<OrderStatus, Long> statusCount = allOrders.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        Order::getStatus,
                        java.util.stream.Collectors.counting()));

        System.out.println("Orders by Status:");
        for (Map.Entry<OrderStatus, Long> entry : statusCount.entrySet()) {
            System.out.printf("  %s: %d orders\n", entry.getKey(), entry.getValue());
        }

        // Loyalty points statistics
        int totalLoyaltyPoints = studentMap.values().stream()
                .mapToInt(Student::getLoyaltyPoints)
                .sum();
        System.out.printf("Total Loyalty Points in System: %d\n", totalLoyaltyPoints);

        // Revenue statistics
        double totalRevenue = allOrders.stream()
                .filter(o -> o.getStatus() == OrderStatus.COMPLETED)
                .mapToDouble(Order::getTotal)
                .sum();
        System.out.printf("Total Revenue: %.2f EGP\n", totalRevenue);
    }

    /**
     * View all loyalty offers (admin)
     */
    private static void viewAllLoyaltyOffers() {
        List<com.university.cafeteria.model.LoyaltyOffer> allOffers = loyaltyOfferManager.getAllOffers();
        if (allOffers.isEmpty()) {
            System.out.println("No loyalty offers created yet.");
            return;
        }

        System.out.println("\n=== All Loyalty Offers (Admin View) ===");
        for (int i = 0; i < allOffers.size(); i++) {
            com.university.cafeteria.model.LoyaltyOffer offer = allOffers.get(i);
            MenuItem menuItem = loyaltyOfferManager.getMenuItemForOffer(offer);
            String itemName = menuItem != null ? menuItem.getName() : "Unknown Item";

            System.out.printf("%d. [ID: %s] %s\n", i + 1, offer.getOfferId(), offer.getDescription());
            System.out.printf("   Item: %s (ID: %s)\n", itemName, offer.getMenuItemId());
            System.out.printf("   Points Required: %d\n", offer.getPointsRequired());
            System.out.printf("   Type: %s\n", offer.getOfferType());
            if (offer.isDiscountOffer()) {
                System.out.printf("   Discount: %.2f EGP\n", offer.getDiscountAmount());
            }
            System.out.printf("   Status: %s\n", offer.isActive() ? "ACTIVE" : "INACTIVE");
            System.out.printf("   Created by: %s\n", offer.getCreatedBy());
            System.out.printf("   Created: %s\n", new java.util.Date(offer.getCreatedTimestamp()));
            System.out.println("─────────────────────────────────");
        }
    }

    /**
     * Create a new loyalty offer (admin)
     */
    private static void createNewLoyaltyOffer() {
        System.out.println("\n=== Create New Loyalty Offer ===");

        // Show available menu items
        List<MenuItem> menuItems = menuManager.getMenuItems();
        if (menuItems.isEmpty()) {
            System.out.println("No menu items available. Please add menu items first.");
            return;
        }

        System.out.println("Available Menu Items:");
        for (int i = 0; i < menuItems.size(); i++) {
            MenuItem item = menuItems.get(i);
            System.out.printf("%d. %s (%.2f EGP) [%s]\n",
                    i + 1, item.getName(), item.getPrice(),
                    item.getCategory().name().replace("_", " "));
        }

        System.out.print("Select menu item (1-" + menuItems.size() + "): ");
        try {
            int itemChoice = Integer.parseInt(scanner.nextLine()) - 1;
            if (itemChoice < 0 || itemChoice >= menuItems.size()) {
                System.out.println("Invalid menu item selection.");
                return;
            }

            MenuItem selectedItem = menuItems.get(itemChoice);

            System.out.print("Enter points required for this offer: ");
            int pointsRequired = Integer.parseInt(scanner.nextLine());
            if (pointsRequired <= 0) {
                System.out.println("Points required must be positive.");
                return;
            }

            System.out.println("Select offer type:");
            System.out.println("1. Free Item");
            System.out.println("2. Discount");
            System.out.print("Choose type (1-2): ");
            String typeChoice = scanner.nextLine();

            String offerType;
            double discountAmount = 0;
            String description;

            if (typeChoice.equals("1")) {
                offerType = "FREE";
                description = String.format("Free %s - %d points", selectedItem.getName(), pointsRequired);
            } else if (typeChoice.equals("2")) {
                offerType = "DISCOUNT";
                System.out.print("Enter discount amount (EGP): ");
                discountAmount = Double.parseDouble(scanner.nextLine());
                if (discountAmount <= 0 || discountAmount >= selectedItem.getPrice()) {
                    System.out.println("Invalid discount amount.");
                    return;
                }
                description = String.format("%.2f EGP discount on %s - %d points",
                        discountAmount, selectedItem.getName(), pointsRequired);
            } else {
                System.out.println("Invalid offer type selection.");
                return;
            }

            System.out.print("Enter custom description (or press Enter to use default): ");
            String customDescription = scanner.nextLine().trim();
            if (!customDescription.isEmpty()) {
                description = customDescription;
            }

            // Create the offer
            com.university.cafeteria.model.LoyaltyOffer newOffer = new com.university.cafeteria.model.LoyaltyOffer(
                    UUID.randomUUID().toString(),
                    selectedItem.getId(),
                    pointsRequired,
                    offerType,
                    discountAmount,
                    description,
                    currentUser.getUsername()
            );

            if (loyaltyOfferManager.createOffer(newOffer)) {
                System.out.println("✓ Loyalty offer created successfully!");
                System.out.printf("Offer: %s\n", description);
                System.out.printf("Points Required: %d\n", pointsRequired);
            } else {
                System.out.println("✗ Failed to create loyalty offer.");
            }

        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter valid numbers.");
        }
    }

    /**
     * Edit an existing loyalty offer (admin)
     */
    private static void editLoyaltyOffer() {
        List<com.university.cafeteria.model.LoyaltyOffer> allOffers = loyaltyOfferManager.getAllOffers();
        if (allOffers.isEmpty()) {
            System.out.println("No loyalty offers to edit.");
            return;
        }

        viewAllLoyaltyOffers();
        System.out.print("Enter offer number to edit (1-" + allOffers.size() + "): ");

        try {
            int choice = Integer.parseInt(scanner.nextLine()) - 1;
            if (choice < 0 || choice >= allOffers.size()) {
                System.out.println("Invalid offer selection.");
                return;
            }

            com.university.cafeteria.model.LoyaltyOffer offer = allOffers.get(choice);
            System.out.println("\n=== Editing Offer: " + offer.getDescription() + " ===");
            System.out.println("Leave fields blank to keep current values.");

            System.out.printf("Current points required: %d\nNew points required: ", offer.getPointsRequired());
            String pointsStr = scanner.nextLine().trim();
            if (!pointsStr.isEmpty()) {
                int newPoints = Integer.parseInt(pointsStr);
                if (newPoints > 0) {
                    offer.setPointsRequired(newPoints);
                }
            }

            if (offer.isDiscountOffer()) {
                System.out.printf("Current discount: %.2f EGP\nNew discount amount: ", offer.getDiscountAmount());
                String discountStr = scanner.nextLine().trim();
                if (!discountStr.isEmpty()) {
                    double newDiscount = Double.parseDouble(discountStr);
                    if (newDiscount > 0) {
                        offer.setDiscountAmount(newDiscount);
                    }
                }
            }

            System.out.printf("Current description: %s\nNew description: ", offer.getDescription());
            String newDesc = scanner.nextLine().trim();
            if (!newDesc.isEmpty()) {
                offer.setDescription(newDesc);
            }

            if (loyaltyOfferManager.updateOffer(offer)) {
                System.out.println("✓ Offer updated successfully!");
            } else {
                System.out.println("✗ Failed to update offer.");
            }

        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter valid numbers.");
        }
    }

    /**
     * Toggle offer active status (admin)
     */
    private static void toggleOfferStatus() {
        List<com.university.cafeteria.model.LoyaltyOffer> allOffers = loyaltyOfferManager.getAllOffers();
        if (allOffers.isEmpty()) {
            System.out.println("No loyalty offers available.");
            return;
        }

        viewAllLoyaltyOffers();
        System.out.print("Enter offer number to toggle status (1-" + allOffers.size() + "): ");

        try {
            int choice = Integer.parseInt(scanner.nextLine()) - 1;
            if (choice < 0 || choice >= allOffers.size()) {
                System.out.println("Invalid offer selection.");
                return;
            }

            com.university.cafeteria.model.LoyaltyOffer offer = allOffers.get(choice);
            boolean currentStatus = offer.isActive();
            String action = currentStatus ? "deactivate" : "activate";

            System.out.printf("Are you sure you want to %s this offer? (y/n): ", action);
            String confirmation = scanner.nextLine().trim();

            if (confirmation.equalsIgnoreCase("y")) {
                boolean success = currentStatus ?
                        loyaltyOfferManager.deactivateOffer(offer.getOfferId()) :
                        loyaltyOfferManager.activateOffer(offer.getOfferId());

                if (success) {
                    System.out.printf("✓ Offer %s successfully!\n", action + "d");
                } else {
                    System.out.printf("✗ Failed to %s offer.\n", action);
                }
            } else {
                System.out.println("Action cancelled.");
            }

        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a valid number.");
        }
    }

    /**
     * Remove a loyalty offer (admin)
     */
    private static void removeLoyaltyOffer() {
        List<com.university.cafeteria.model.LoyaltyOffer> allOffers = loyaltyOfferManager.getAllOffers();
        if (allOffers.isEmpty()) {
            System.out.println("No loyalty offers to remove.");
            return;
        }

        viewAllLoyaltyOffers();
        System.out.print("Enter offer number to remove (1-" + allOffers.size() + "): ");

        try {
            int choice = Integer.parseInt(scanner.nextLine()) - 1;
            if (choice < 0 || choice >= allOffers.size()) {
                System.out.println("Invalid offer selection.");
                return;
            }

            com.university.cafeteria.model.LoyaltyOffer offer = allOffers.get(choice);
            System.out.printf("Are you sure you want to permanently remove '%s'? (y/n): ",
                    offer.getDescription());
            String confirmation = scanner.nextLine().trim();

            if (confirmation.equalsIgnoreCase("y")) {
                if (loyaltyOfferManager.removeOffer(offer.getOfferId())) {
                    System.out.println("✓ Offer removed successfully!");
                } else {
                    System.out.println("✗ Failed to remove offer.");
                }
            } else {
                System.out.println("Removal cancelled.");
            }

        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a valid number.");
        }
    }

    /**
     * View loyalty offer analytics (admin)
     */
    private static void viewOfferAnalytics() {
        List<com.university.cafeteria.model.LoyaltyOffer> allOffers = loyaltyOfferManager.getAllOffers();
        List<com.university.cafeteria.model.LoyaltyOffer> activeOffers = loyaltyOfferManager.getActiveOffers();

        System.out.println("\n=== Loyalty Offer Analytics ===");
        System.out.printf("Total Offers Created: %d\n", allOffers.size());
        System.out.printf("Active Offers: %d\n", activeOffers.size());
        System.out.printf("Inactive Offers: %d\n", allOffers.size() - activeOffers.size());

        if (activeOffers.isEmpty()) {
            System.out.println("No active offers to analyze.");
            return;
        }

        // Analyze offers by type
        long freeOffers = activeOffers.stream().filter(com.university.cafeteria.model.LoyaltyOffer::isFreeItemOffer).count();
        long discountOffers = activeOffers.stream().filter(com.university.cafeteria.model.LoyaltyOffer::isDiscountOffer).count();

        System.out.println("\nOffer Types:");
        System.out.printf("  Free Item Offers: %d\n", freeOffers);
        System.out.printf("  Discount Offers: %d\n", discountOffers);

        // Point requirements analysis
        OptionalDouble avgPoints = activeOffers.stream().mapToInt(com.university.cafeteria.model.LoyaltyOffer::getPointsRequired).average();
        int minPoints = activeOffers.stream().mapToInt(com.university.cafeteria.model.LoyaltyOffer::getPointsRequired).min().orElse(0);
        int maxPoints = activeOffers.stream().mapToInt(com.university.cafeteria.model.LoyaltyOffer::getPointsRequired).max().orElse(0);

        System.out.println("\nPoints Analysis:");
        System.out.printf("  Average Points Required: %.1f\n", avgPoints.orElse(0));
        System.out.printf("  Minimum Points Required: %d\n", minPoints);
        System.out.printf("  Maximum Points Required: %d\n", maxPoints);

        // Show offers by menu category
        Map<String, Long> offersByCategory = new HashMap<>();
        for (com.university.cafeteria.model.LoyaltyOffer offer : activeOffers) {
            MenuItem item = loyaltyOfferManager.getMenuItemForOffer(offer);
            if (item != null) {
                String category = item.getCategory().name().replace("_", " ");
                offersByCategory.put(category, offersByCategory.getOrDefault(category, 0L) + 1);
            }
        }

        if (!offersByCategory.isEmpty()) {
            System.out.println("\nOffers by Menu Category:");
            for (Map.Entry<String, Long> entry : offersByCategory.entrySet()) {
                System.out.printf("  %s: %d offer(s)\n", entry.getKey(), entry.getValue());
            }
        }
    }

    // =================== PASSWORD MANAGEMENT ===================

    /**
     * Change password for current user
     */
    private static void changePassword() {
        System.out.println("\n=== Change Password ===");
        System.out.print("Enter current password: ");
        String currentPassword = scanner.nextLine();

        System.out.print("Enter new password: ");
        String newPassword = scanner.nextLine();

        System.out.print("Confirm new password: ");
        String confirmPassword = scanner.nextLine();

        if (!newPassword.equals(confirmPassword)) {
            System.out.println("✗ New passwords do not match!");
            return;
        }

        if (newPassword.length() < 6) {
            System.out.println("✗ Password must be at least 6 characters long!");
            return;
        }

        if (userManager.changePassword(currentUser.getUsername(), currentPassword, newPassword)) {
            System.out.println("✓ Password changed successfully!");
        } else {
            System.out.println("✗ Failed to change password. Please check your current password.");
        }
    }

    // =================== ADMIN MANAGEMENT (SYSTEM ADMIN ONLY) ===================

    /**
     * Show admin management menu (system admin only)
     */
    private static void showAdminManagementMenu() {
        while (true) {
            System.out.println("\n=== Admin Management (System Admin Only) ===");
            System.out.println("1. View Pending Admin Registrations");
            System.out.println("2. Approve Admin Registration");
            System.out.println("3. Reject Admin Registration");
            System.out.println("4. View All Approved Admins");
            System.out.println("5. Register New Admin Account");
            System.out.println("6. Back to Admin Menu");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine();
            switch (choice) {
                case "1":
                    viewPendingAdminRegistrations();
                    break;
                case "2":
                    approveAdminRegistration();
                    break;
                case "3":
                    rejectAdminRegistration();
                    break;
                case "4":
                    viewAllApprovedAdmins();
                    break;
                case "5":
                    registerNewAdminAccount();
                    break;
                case "6":
                    return;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    /**
     * View pending admin registrations awaiting approval
     */
    private static void viewPendingAdminRegistrations() {
        List<Admin> pendingAdmins = userManager.getPendingAdminApprovals(currentUser.getUsername());

        System.out.println("\n=== Pending Admin Registrations ===");
        if (pendingAdmins.isEmpty()) {
            System.out.println("No pending admin registrations.");
            return;
        }

        System.out.println("+----+------------------+----------------------+---------------------+");
        System.out.println("| #  | Username         | Full Name            | Registration Date   |");
        System.out.println("+----+------------------+----------------------+---------------------+");

        for (int i = 0; i < pendingAdmins.size(); i++) {
            Admin admin = pendingAdmins.get(i);
            String regDate = admin.getRegistrationDate().substring(0, 19); // Remove microseconds
            System.out.printf("| %-2d | %-16s | %-20s | %-19s |\n",
                    i + 1, admin.getUsername(), admin.getName(), regDate);
        }
        System.out.println("+----+------------------+----------------------+---------------------+");
    }

    /**
     * Approve a pending admin registration
     */
    private static void approveAdminRegistration() {
        List<Admin> pendingAdmins = userManager.getPendingAdminApprovals(currentUser.getUsername());

        if (pendingAdmins.isEmpty()) {
            System.out.println("No pending admin registrations to approve.");
            return;
        }

        viewPendingAdminRegistrations();
        System.out.print("\nEnter username to approve: ");
        String username = scanner.nextLine();

        boolean found = false;
        for (Admin admin : pendingAdmins) {
            if (admin.getUsername().equals(username)) {
                found = true;
                break;
            }
        }

        if (!found) {
            System.out.println("✗ Username not found in pending registrations.");
            return;
        }

        System.out.printf("Are you sure you want to approve admin '%s'? (y/n): ", username);
        String confirmation = scanner.nextLine().trim();

        if (confirmation.equalsIgnoreCase("y")) {
            if (userManager.approveAdmin(username, currentUser.getUsername())) {
                System.out.println("✓ Admin approved successfully!");
            } else {
                System.out.println("✗ Failed to approve admin.");
            }
        } else {
            System.out.println("Approval cancelled.");
        }
    }

    /**
     * Reject a pending admin registration
     */
    private static void rejectAdminRegistration() {
        List<Admin> pendingAdmins = userManager.getPendingAdminApprovals(currentUser.getUsername());

        if (pendingAdmins.isEmpty()) {
            System.out.println("No pending admin registrations to reject.");
            return;
        }

        viewPendingAdminRegistrations();
        System.out.print("\nEnter username to reject: ");
        String username = scanner.nextLine();

        boolean found = false;
        for (Admin admin : pendingAdmins) {
            if (admin.getUsername().equals(username)) {
                found = true;
                break;
            }
        }

        if (!found) {
            System.out.println("✗ Username not found in pending registrations.");
            return;
        }

        System.out.printf("Are you sure you want to reject admin '%s'? (y/n): ", username);
        String confirmation = scanner.nextLine().trim();

        if (confirmation.equalsIgnoreCase("y")) {
            if (userManager.rejectAdmin(username, currentUser.getUsername())) {
                System.out.println("✓ Admin registration rejected.");
            } else {
                System.out.println("✗ Failed to reject admin registration.");
            }
        } else {
            System.out.println("Rejection cancelled.");
        }
    }

    /**
     * View all approved admins
     */
    private static void viewAllApprovedAdmins() {
        List<Admin> approvedAdmins = userManager.getAllAdmins(currentUser.getUsername());

        System.out.println("\n=== All Approved Admins ===");
        if (approvedAdmins.isEmpty()) {
            System.out.println("No approved admins found.");
            return;
        }

        System.out.println("+----+------------------+----------------------+---------------------+------------------+");
        System.out.println("| #  | Username         | Full Name            | Registration Date   | Approved By      |");
        System.out.println("+----+------------------+----------------------+---------------------+------------------+");

        for (int i = 0; i < approvedAdmins.size(); i++) {
            Admin admin = approvedAdmins.get(i);
            String regDate = admin.getRegistrationDate().substring(0, 19); // Remove microseconds
            String approvedBy = admin.getApprovedBy() != null ? admin.getApprovedBy() : "N/A";
            System.out.printf("| %-2d | %-16s | %-20s | %-19s | %-16s |\n",
                    i + 1, admin.getUsername(), admin.getName(), regDate, approvedBy);
        }
        System.out.println("+----+------------------+----------------------+---------------------+------------------+");
    }

    /**
     * Register a new admin account (system admin only)
     */
    private static void registerNewAdminAccount() {
        System.out.println("\n=== Register New Admin Account ===");
        System.out.print("Enter username: ");
        String username = scanner.nextLine();

        System.out.print("Enter full name: ");
        String name = scanner.nextLine();

        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        System.out.print("Confirm password: ");
        String confirmPassword = scanner.nextLine();

        if (!password.equals(confirmPassword)) {
            System.out.println("✗ Passwords do not match!");
            return;
        }

        if (password.length() < 6) {
            System.out.println("✗ Password must be at least 6 characters long!");
            return;
        }

        if (userManager.registerAdmin(username, password, name)) {
            System.out.println("✓ Admin account registered successfully!");
            System.out.println("The admin will be automatically approved since you are the system admin.");

            // Auto-approve since system admin is registering
            userManager.approveAdmin(username, currentUser.getUsername());
        } else {
            System.out.println("✗ Failed to register admin. Username may already exist.");
        }
    }

    // =================== MAIN MENU ENHANCEMENTS ===================

    /**
     * Register admin request (requires system admin approval)
     */
    private static void registerAdminRequest() {
        System.out.println("\n=== Request Admin Registration ===");
        System.out.println("Note: Admin registrations require approval from the system administrator.");
        System.out.print("Enter desired username: ");
        String username = scanner.nextLine();

        System.out.print("Enter your full name: ");
        String name = scanner.nextLine();

        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        System.out.print("Confirm password: ");
        String confirmPassword = scanner.nextLine();

        if (!password.equals(confirmPassword)) {
            System.out.println("✗ Passwords do not match!");
            return;
        }

        if (password.length() < 6) {
            System.out.println("✗ Password must be at least 6 characters long!");
            return;
        }

        if (userManager.registerAdmin(username, password, name)) {
            System.out.println("✓ Admin registration request submitted successfully!");
            System.out.println("Your request will be reviewed by the system administrator.");
            System.out.println("You will be able to login once your request is approved.");
        } else {
            System.out.println("✗ Failed to submit registration request. Username may already exist or be pending approval.");
        }
    }
}
