package com.university.cafeteria.service;

import com.university.cafeteria.model.Order;
import com.university.cafeteria.enums.OrderStatus;
import java.util.*;
import java.time.*;
import java.time.temporal.WeekFields;

public class ReportGenerator {
    public void printDailySales(List<Order> orders) {
        LocalDate today = LocalDate.now();
        ZoneId zone = ZoneId.systemDefault();
        double totalSales = 0;
        long completedOrders = 0;
        for (Order o : orders) {
            if (o.getStatus() == OrderStatus.COMPLETED) {
                LocalDate orderDate = Instant.ofEpochMilli(o.getTimestamp()).atZone(zone).toLocalDate();
                if (orderDate.equals(today)) {
                    totalSales += o.getTotal();
                    completedOrders++;
                }
            }
        }
        System.out.println("--- Daily Sales Report (" + today + ") ---");
        System.out.printf("Total Sales: EGP %.2f%n", totalSales);
        System.out.println("Completed Orders: " + completedOrders);
    }

    public void printWeeklySales(List<Order> orders) {
        LocalDate today = LocalDate.now();
        WeekFields wf = WeekFields.of(Locale.getDefault());
        int currentWeek = today.get(wf.weekOfWeekBasedYear());
        int currentYear = today.getYear();
        ZoneId zone = ZoneId.systemDefault();
        double totalSales = 0;
        long completedOrders = 0;
        Map<LocalDate, Double> dailyTotals = new TreeMap<>();
        for (Order o : orders) {
            if (o.getStatus() == OrderStatus.COMPLETED) {
                LocalDate orderDate = Instant.ofEpochMilli(o.getTimestamp()).atZone(zone).toLocalDate();
                int orderWeek = orderDate.get(wf.weekOfWeekBasedYear());
                int orderYear = orderDate.getYear();
                if (orderWeek == currentWeek && orderYear == currentYear) {
                    totalSales += o.getTotal();
                    completedOrders++;
                    dailyTotals.merge(orderDate, o.getTotal(), Double::sum);
                }
            }
        }
    System.out.println("--- Weekly Sales Report (Week " + currentWeek + ", " + currentYear + ") ---");
        for (Map.Entry<LocalDate, Double> e : dailyTotals.entrySet()) {
            System.out.printf("%s : EGP %.2f%n", e.getKey(), e.getValue());
        }
        System.out.printf("TOTAL WEEK SALES: EGP %.2f%n", totalSales);
        System.out.println("Completed Orders: " + completedOrders);
    }

    public void printLoyaltyRedemptions(List<Order> orders) {
        long redemptions = orders.stream().filter(Order::isUsedPoints).count();
        int totalPoints = orders.stream().mapToInt(Order::getPointsRedeemed).sum();
        System.out.println("--- Loyalty Redemptions ---");
        System.out.println("Total Redemptions: " + redemptions);
        System.out.println("Total Points Redeemed: " + totalPoints);
    }
}
