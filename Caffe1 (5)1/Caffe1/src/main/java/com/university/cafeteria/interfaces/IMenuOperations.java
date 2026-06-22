package com.university.cafeteria.interfaces;

import com.university.cafeteria.model.MenuItem;
import java.util.List;

public interface IMenuOperations {
    boolean addMenuItem(MenuItem item);
    boolean editMenuItem(MenuItem item);
    boolean removeMenuItem(String itemId);
    List<MenuItem> getMenuItems();
}
