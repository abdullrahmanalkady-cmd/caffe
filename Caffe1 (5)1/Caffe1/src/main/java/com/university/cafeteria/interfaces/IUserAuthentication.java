package com.university.cafeteria.interfaces;

import com.university.cafeteria.model.User;

public interface IUserAuthentication {
    User login(String username, String password);
    boolean register(User user, String password);
}
