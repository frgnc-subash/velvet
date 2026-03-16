package org.velvet.controller;

import org.velvet.exception.InvalidLoginException;
import org.velvet.model.service.UserService;
import org.velvet.model.user.Customer;
import org.velvet.model.user.User;

public class LoginController {
    private final UserService userService;

    public LoginController(UserService userService) {
        this.userService = userService;
    }

    public User login(String username, String password) throws InvalidLoginException {
        try {
            return userService.login(username, password);
        } catch (IllegalArgumentException | IllegalStateException e) {
            throw new InvalidLoginException(e.getMessage());
        }
    }

    public Customer registerCustomer(String name, String username, String password, String phone, String email) {
        return userService.registerCustomer(name, username, password, phone, email);
    }
}
