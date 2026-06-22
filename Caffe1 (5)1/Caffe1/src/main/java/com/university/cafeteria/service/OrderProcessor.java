package com.university.cafeteria.service;

import com.university.cafeteria.model.Order;
import com.university.cafeteria.enums.OrderStatus;
import com.university.cafeteria.interfaces.IOrderOperations;
import com.university.cafeteria.interfaces.ILoyaltyProgram;
import com.university.cafeteria.interfaces.INotificationService;
import java.util.*;
import java.util.stream.Collectors;

public class OrderProcessor implements IOrderOperations {
    private Map<String, Order> orders = new HashMap<>();
    private ILoyaltyProgram loyaltyProgram;
    private INotificationService notificationService;

    public OrderProcessor(ILoyaltyProgram loyaltyProgram, INotificationService notificationService) {
        this.loyaltyProgram = loyaltyProgram;
        this.notificationService = notificationService;
        loadOrderData();
    }

    private void loadOrderData() {
        List<Order> orderList = DataManager.loadOrders();
        orders.clear();
        for (Order order : orderList) {
            orders.put(order.getId(), order);
        }
    }

    private void saveOrderData() {
        DataManager.saveAllOrders(new ArrayList<>(orders.values()));
    }

    @Override
    public Order placeOrder(Order order) {
        orders.put(order.getId(), order);
        if (order.getStatus() == OrderStatus.PENDING) {
            // Add points based on the final total (after any discounts applied)
            loyaltyProgram.addPoints(order.getUserId(), order.getTotal());
        }
        saveOrderData(); // Save changes
        return order;
    }

    @Override
    public List<Order> getOrdersByUserId(String userId) {
        return orders.values().stream()
                .filter(o -> o.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Order> getOrdersByStatus(String status) {
        return orders.values().stream()
                .filter(o -> o.getStatus().name().equalsIgnoreCase(status))
                .collect(Collectors.toList());
    }

    @Override
    public boolean updateOrderStatus(String orderId, String status) {
        Order order = orders.get(orderId);
        if (order == null) return false;
        try {
            OrderStatus newStatus = OrderStatus.valueOf(status);
            order.setStatus(newStatus);
            if (newStatus == OrderStatus.READY_FOR_PICKUP) {
                notificationService.notifyUser(order.getUserId(), "Your order is ready for pickup!");
            }
            saveOrderData(); // Save changes
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public List<Order> getAllOrders() {
        return new ArrayList<>(orders.values());
    }

    public Order getOrderById(String orderId) {
        return orders.get(orderId);
    }
}
