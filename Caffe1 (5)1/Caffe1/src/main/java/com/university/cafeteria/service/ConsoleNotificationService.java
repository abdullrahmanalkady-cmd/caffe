package com.university.cafeteria.service;

import com.university.cafeteria.interfaces.INotificationService;

public class ConsoleNotificationService implements INotificationService {
    @Override
    public void notifyUser(String userId, String message) {
        System.out.println("[Notification to User " + userId + "]: " + message);
    }
}
