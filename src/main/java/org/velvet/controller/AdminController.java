package org.velvet.controller;

import org.velvet.model.booking.Booking;
import org.velvet.model.service.BookingService;
import org.velvet.model.service.UserService;
import org.velvet.model.user.User;

import java.util.List;

public class AdminController {
    private final UserService userService;
    private final BookingService bookingService;

    public AdminController(UserService userService, BookingService bookingService) {
        this.userService = userService;
        this.bookingService = bookingService;
    }

    public User addScheduler(String name, String username, String password, String phone, String email) {
        return userService.addUser(name, username, password, User.ROLE_SCHEDULER, phone, email);
    }

    public void updateScheduler(String userId, String name, String username, String password, String phone, String email, boolean blocked) {
        User user = userService.findById(userId);
        if (user == null || !User.ROLE_SCHEDULER.equalsIgnoreCase(user.getRole())) {
            throw new IllegalArgumentException("Scheduler not found.");
        }
        userService.updateUser(userId, name, username, password, phone, email, blocked);
    }

    public void deleteScheduler(String userId) {
        User user = userService.findById(userId);
        if (user == null || !User.ROLE_SCHEDULER.equalsIgnoreCase(user.getRole())) {
            throw new IllegalArgumentException("Scheduler not found.");
        }
        userService.deleteUser(userId);
    }

    public List<User> getSchedulers(String keyword) {
        return userService.searchUsers(keyword).stream()
                .filter(user -> User.ROLE_SCHEDULER.equalsIgnoreCase(user.getRole()))
                .toList();
    }

    public List<User> searchUsers(String keyword) {
        return userService.searchUsers(keyword);
    }

    public void blockUser(String userId) {
        userService.setBlocked(userId, true);
    }

    public void unblockUser(String userId) {
        userService.setBlocked(userId, false);
    }

    public void deleteUser(String userId) {
        userService.deleteUser(userId);
    }

    public List<Booking> filterBookings(String filter, String keyword) {
        return bookingService.filterBookingsForAdmin(filter, keyword);
    }
}
