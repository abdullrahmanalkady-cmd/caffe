package com.university.cafeteria.interfaces;

import com.university.cafeteria.model.Order;
import java.util.List;

public interface IOrderOperations {
    Order placeOrder(Order order);
    List<Order> getOrdersByUserId(String userId);
    List<Order> getOrdersByStatus(String status);
    boolean updateOrderStatus(String orderId, String status);
}
