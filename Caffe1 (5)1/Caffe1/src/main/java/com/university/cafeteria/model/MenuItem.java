package com.university.cafeteria.model;

import com.university.cafeteria.enums.MenuItemCategory;

public class MenuItem {
    private String id;
    private String name;
    private String description;
    private double price;
    private MenuItemCategory category;

    public MenuItem(String id, String name, String description, double price, MenuItemCategory category) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public double getPrice() { return price; }
    public MenuItemCategory getCategory() { return category; }

    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setPrice(double price) { this.price = price; }
    public void setCategory(MenuItemCategory category) { this.category = category; }
}
