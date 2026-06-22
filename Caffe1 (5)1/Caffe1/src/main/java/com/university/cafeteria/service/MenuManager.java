package com.university.cafeteria.service;

import com.university.cafeteria.model.MenuItem;
import com.university.cafeteria.enums.MenuItemCategory;
import com.university.cafeteria.interfaces.IMenuOperations;
import java.util.*;
import java.util.stream.Collectors;

public class MenuManager implements IMenuOperations {
    private Map<String, MenuItem> menuItems = new HashMap<>();

    public MenuManager() {
        // Initialize data files
        DataManager.initializeDataFiles();
        
        // Load existing menu items
        menuItems = DataManager.loadMenuItems();
        
        // If no menu items exist, create default menu
        if (menuItems.isEmpty()) {
            createDefaultMenu();
        }
    }
    
    /**
     * Create default menu items
     */
    private void createDefaultMenu() {
        // Main Course Items (6 items)
        addMenuItem(new MenuItem(UUID.randomUUID().toString(), "Grilled Chicken Sandwich", "Tender grilled chicken breast with lettuce, tomato, and special sauce", 35.0, MenuItemCategory.MAIN_COURSE));
        addMenuItem(new MenuItem(UUID.randomUUID().toString(), "Beef Burger", "Juicy beef patty with cheese, pickles, onions, and fries", 45.0, MenuItemCategory.MAIN_COURSE));
        addMenuItem(new MenuItem(UUID.randomUUID().toString(), "Caesar Salad", "Fresh romaine lettuce with caesar dressing, croutons, and parmesan", 28.0, MenuItemCategory.MAIN_COURSE));
        addMenuItem(new MenuItem(UUID.randomUUID().toString(), "Margherita Pizza", "Classic pizza with tomato sauce, mozzarella, and fresh basil", 40.0, MenuItemCategory.MAIN_COURSE));
        addMenuItem(new MenuItem(UUID.randomUUID().toString(), "Fish & Chips", "Crispy battered fish served with golden fries and tartar sauce", 38.0, MenuItemCategory.MAIN_COURSE));
        addMenuItem(new MenuItem(UUID.randomUUID().toString(), "Pasta Alfredo", "Creamy fettuccine pasta with grilled chicken and alfredo sauce", 42.0, MenuItemCategory.MAIN_COURSE));
        
        // Dessert Items (5 items)
        addMenuItem(new MenuItem(UUID.randomUUID().toString(), "Chocolate Fudge Cake", "Rich chocolate cake with fudge frosting and chocolate chips", 25.0, MenuItemCategory.DESSERT));
        addMenuItem(new MenuItem(UUID.randomUUID().toString(), "Cheesecake", "New York style cheesecake with berry compote", 22.0, MenuItemCategory.DESSERT));
        addMenuItem(new MenuItem(UUID.randomUUID().toString(), "Apple Pie", "Homemade apple pie with cinnamon and vanilla ice cream", 20.0, MenuItemCategory.DESSERT));
        addMenuItem(new MenuItem(UUID.randomUUID().toString(), "Tiramisu", "Classic Italian dessert with coffee and mascarpone", 28.0, MenuItemCategory.DESSERT));
        addMenuItem(new MenuItem(UUID.randomUUID().toString(), "Brownie Sundae", "Warm chocolate brownie with vanilla ice cream and hot fudge", 24.0, MenuItemCategory.DESSERT));
        
        // Drink Items (6 items)
        addMenuItem(new MenuItem(UUID.randomUUID().toString(), "Fresh Orange Juice", "Freshly squeezed orange juice, vitamin C rich", 15.0, MenuItemCategory.DRINK));
        addMenuItem(new MenuItem(UUID.randomUUID().toString(), "Cappuccino", "Rich espresso with steamed milk and foam", 18.0, MenuItemCategory.DRINK));
        addMenuItem(new MenuItem(UUID.randomUUID().toString(), "Iced Latte", "Cold espresso with milk and ice, perfect for hot days", 20.0, MenuItemCategory.DRINK));
        addMenuItem(new MenuItem(UUID.randomUUID().toString(), "Lemonade", "Fresh homemade lemonade with mint leaves", 12.0, MenuItemCategory.DRINK));
        addMenuItem(new MenuItem(UUID.randomUUID().toString(), "Mango Smoothie", "Tropical mango smoothie with yogurt and honey", 22.0, MenuItemCategory.DRINK));
        addMenuItem(new MenuItem(UUID.randomUUID().toString(), "Green Tea", "Premium green tea with antioxidants", 10.0, MenuItemCategory.DRINK));
        
        // Snack Items (6 items)
        addMenuItem(new MenuItem(UUID.randomUUID().toString(), "Chicken Wings", "Spicy buffalo wings with blue cheese dip", 30.0, MenuItemCategory.SNACK));
        addMenuItem(new MenuItem(UUID.randomUUID().toString(), "Nachos Supreme", "Tortilla chips with cheese, jalapeños, and sour cream", 25.0, MenuItemCategory.SNACK));
        addMenuItem(new MenuItem(UUID.randomUUID().toString(), "Mozzarella Sticks", "Crispy breaded mozzarella with marinara sauce", 18.0, MenuItemCategory.SNACK));
        addMenuItem(new MenuItem(UUID.randomUUID().toString(), "Potato Wedges", "Seasoned potato wedges with garlic aioli", 16.0, MenuItemCategory.SNACK));
        addMenuItem(new MenuItem(UUID.randomUUID().toString(), "Onion Rings", "Golden crispy onion rings with ranch dressing", 14.0, MenuItemCategory.SNACK));
        addMenuItem(new MenuItem(UUID.randomUUID().toString(), "Spring Rolls", "Vegetable spring rolls with sweet chili sauce", 20.0, MenuItemCategory.SNACK));
        
        // Save the default menu
        saveMenuData();
    }
    
    /**
     * Save menu data to file
     */
    private void saveMenuData() {
        DataManager.saveMenuItems(menuItems);
    }

    @Override
    public boolean addMenuItem(MenuItem item) {
        if (menuItems.containsKey(item.getId())) return false;
        menuItems.put(item.getId(), item);
        saveMenuData(); // Save changes
        return true;
    }

    @Override
    public boolean editMenuItem(MenuItem item) {
        if (!menuItems.containsKey(item.getId())) return false;
        menuItems.put(item.getId(), item);
        saveMenuData(); // Save changes
        return true;
    }

    @Override
    public boolean removeMenuItem(String itemId) {
        boolean removed = menuItems.remove(itemId) != null;
        if (removed) {
            saveMenuData(); // Save changes
        }
        return removed;
    }

    @Override
    public List<MenuItem> getMenuItems() {
        return new ArrayList<>(menuItems.values());
    }

    public MenuItem getMenuItemById(String id) {
        return menuItems.get(id);
    }
}
