package org.velvet.model.controller;

import org.velvet.model.booking.Booking;
import org.velvet.model.issue.Issue;
import org.velvet.model.issue.IssueStatus;
import org.velvet.model.service.IssueService;
import org.velvet.model.service.ReportService;
import org.velvet.model.service.UserService;
import org.velvet.model.user.User;

import java.time.LocalDate;
import java.util.List;

public class ManagerController {
    private final ReportService reportService;
    private final IssueService issueService;
    private final UserService userService;

    public ManagerController(ReportService reportService, IssueService issueService, UserService userService) {
        this.reportService = reportService;
        this.issueService = issueService;
        this.userService = userService;
    }

    public double getWeeklySales() {
        return reportService.getWeeklySales();
    }

    public double getMonthlySales() {
        return reportService.getMonthlySales();
    }

    public double getYearlySales() {
        return reportService.getYearlySales();
    }

    public List<Booking> getSalesBookings(LocalDate fromDate, LocalDate toDate) {
        return reportService.getBookingsBetween(fromDate, toDate);
    }

    public List<Issue> searchIssues(String keyword) {
        return issueService.searchIssues(keyword);
    }

    public void assignScheduler(String issueId, String schedulerId, String response) {
        issueService.assignScheduler(issueId, schedulerId, response);
    }

    public void updateIssueStatus(String issueId, IssueStatus status, String response) {
        issueService.updateStatus(issueId, status, response);
    }

    public List<User> getSchedulers() {
        return userService.getUsersByRole(User.ROLE_SCHEDULER);
    }
}
