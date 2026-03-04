package org.velvet.model.controller;

import org.velvet.model.booking.Booking;
import org.velvet.model.hall.Hall;
import org.velvet.model.issue.MaintenanceTask;
import org.velvet.model.service.BookingService;
import org.velvet.model.service.HallService;

import java.time.LocalDateTime;
import java.util.List;

public class SchedulerController {
    private final HallService hallService;
    private final BookingService bookingService;

    public SchedulerController(HallService hallService, BookingService bookingService) {
        this.hallService = hallService;
        this.bookingService = bookingService;
    }

    public Hall addHall(String type, String name, LocalDateTime availableFrom, LocalDateTime availableTo, String remarks) {
        return hallService.addHall(type, name, availableFrom, availableTo, remarks);
    }

    public void updateHall(String hallId, String type, String name, LocalDateTime availableFrom, LocalDateTime availableTo, String remarks) {
        hallService.updateHall(hallId, type, name, availableFrom, availableTo, remarks);
    }

    public void deleteHall(String hallId) {
        hallService.deleteHall(hallId);
    }

    public void setAvailability(String hallId, LocalDateTime from, LocalDateTime to, String remarks) {
        hallService.setAvailability(hallId, from, to, remarks);
    }

    public List<Hall> searchHalls(String keyword) {
        return hallService.searchHalls(keyword);
    }

    public List<MaintenanceTask> searchMaintenanceTasks(String keyword) {
        return hallService.searchMaintenanceTasks(keyword);
    }

    public MaintenanceTask addMaintenanceTask(String hallId, LocalDateTime start, LocalDateTime end, String remarks, String schedulerId) {
        return hallService.addMaintenanceTask(hallId, start, end, remarks, schedulerId);
    }

    public void updateMaintenanceTask(String taskId, String hallId, LocalDateTime start, LocalDateTime end, String remarks) {
        hallService.updateMaintenanceTask(taskId, hallId, start, end, remarks);
    }

    public void deleteMaintenanceTask(String taskId) {
        hallService.deleteMaintenanceTask(taskId);
    }

    public List<Booking> getAllBookings() {
        return bookingService.getAllBookings();
    }
}
