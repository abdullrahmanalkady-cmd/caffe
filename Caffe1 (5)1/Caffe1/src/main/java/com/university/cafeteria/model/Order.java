package com.university.cafeteria.model;

import com.university.cafeteria.enums.OrderStatus;
import java.util.List;

public class Order {
    private String id;
    private String userId;
    private List<MenuItem> items;
    private double total;
    private OrderStatus status;
    private long timestamp;
    private boolean usedPoints;
    private int pointsRedeemed;

    public Order(String id, String userId, List<MenuItem> items, double total, OrderStatus status, long timestamp, boolean usedPoints, int pointsRedeemed) {
        this.id = id;
        this.userId = userId;
        this.items = items;
        this.total = total;
        this.status = status;
        this.timestamp = timestamp;
        this.usedPoints = usedPoints;
        this.pointsRedeemed = pointsRedeemed;
    }

    public String getId() { return id; }
    public String getUserId() { return userId; }
    public List<MenuItem> getItems() { return items; }
    public double getTotal() { return total; }
    public OrderStatus getStatus() { return status; }
    public long getTimestamp() { return timestamp; }
    public boolean isUsedPoints() { return usedPoints; }
    public int getPointsRedeemed() { return pointsRedeemed; }

    public void setStatus(OrderStatus status) { this.status = status; }
}
