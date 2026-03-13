package org.velvet.controller;

import org.velvet.exception.BookingNotAllowedException;
import org.velvet.exception.PaymentFailedException;
import org.velvet.model.booking.Booking;
import org.velvet.model.hall.Hall;
import org.velvet.model.issue.Issue;
import org.velvet.model.payment.Payment;
import org.velvet.model.payment.Receipt;
import org.velvet.model.service.BookingService;
import org.velvet.model.service.HallService;
import org.velvet.model.service.IssueService;
import org.velvet.model.service.PaymentService;
import org.velvet.model.service.UserService;
import org.velvet.model.user.Customer;

import java.time.LocalDateTime;
import java.util.List;

public class CustomerController {
    private final UserService userService;
    private final HallService hallService;
    private final BookingService bookingService;
    private final PaymentService paymentService;
    private final IssueService issueService;

    public CustomerController(UserService userService, HallService hallService, BookingService bookingService,
                              PaymentService paymentService, IssueService issueService) {
        this.userService = userService;
        this.hallService = hallService;
        this.bookingService = bookingService;
        this.paymentService = paymentService;
        this.issueService = issueService;
    }

    public void updateProfile(String userId, String name, String phone, String email, String password) {
        userService.updateProfile(userId, name, phone, email, password);
    }

    public List<Hall> searchHalls(String keyword) {
        return hallService.searchHalls(keyword);
    }

    public Booking createBooking(Customer customer, String hallId, LocalDateTime start, LocalDateTime end) {
        return bookingService.createBooking(customer, hallId, start, end, hallService);
    }

    public Payment processPayment(Booking booking, String method) throws PaymentFailedException {
        Payment payment = paymentService.processPayment(booking, method);
        bookingService.confirmBooking(booking.getId());
        booking.confirmBooking();
        return payment;
    }

    public Receipt generateReceipt(Booking booking, Payment payment) {
        return paymentService.generateReceipt(booking, payment);
    }

    public void cancelBooking(String bookingId, String customerId) throws BookingNotAllowedException {
        bookingService.cancelBooking(bookingId, customerId);
    }

    public List<Booking> getAllCustomerBookings(String customerId) {
        return bookingService.getBookingsByCustomer(customerId);
    }

    public List<Booking> getUpcomingCustomerBookings(String customerId) {
        return bookingService.getUpcomingBookingsByCustomer(customerId);
    }

    public List<Booking> getPastCustomerBookings(String customerId) {
        return bookingService.getPastBookingsByCustomer(customerId);
    }

    public Issue raiseIssue(String bookingId, String customerId, String hallId, String description) {
        return issueService.raiseIssue(bookingId, customerId, hallId, description);
    }

    public List<Issue> getCustomerIssues(String customerId) {
        return issueService.getIssuesByCustomer(customerId);
    }
}
