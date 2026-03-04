package org.velvet.model.booking;

import org.velvet.util.DateTimeUtil;

import java.time.Duration;
import java.time.LocalDateTime;

public class Booking {
    private String id;
    private String customerId;
    private String customerName;
    private String hallId;
    private String hallName;
    private String hallType;
    private LocalDateTime start;
    private LocalDateTime end;
    private double totalAmount;
    private BookingStatus status;
    private LocalDateTime createdAt;

    public Booking() {
    }

    public Booking(String id, String customerId, String customerName, String hallId, String hallName,
                   String hallType, LocalDateTime start, LocalDateTime end, double totalAmount,
                   BookingStatus status, LocalDateTime createdAt) {
        this.id = id;
        this.customerId = customerId;
        this.customerName = customerName;
        this.hallId = hallId;
        this.hallName = hallName;
        this.hallType = hallType;
        this.start = start;
        this.end = end;
        this.totalAmount = totalAmount;
        this.status = status;
        this.createdAt = createdAt;
    }

    public TimeSlot getTimeSlot() {
        return new TimeSlot(start, end);
    }

    public double calculateDuration() {
        return Duration.between(start, end).toMinutes() / 60.0;
    }

    public double calculateTotalAmount(double hallRatePerHour) {
        return Math.round(calculateDuration() * hallRatePerHour * 100.0) / 100.0;
    }

    public void confirmBooking() {
        this.status = BookingStatus.CONFIRMED;
    }

    public void cancelBooking() {
        this.status = BookingStatus.CANCELLED;
    }

    public boolean isCancellable(int minimumDaysBefore) {
        return start != null && LocalDateTime.now().plusDays(minimumDaysBefore).isBefore(start);
    }

    public String toRecord() {
        return String.join("|",
                safe(id), safe(customerId), safe(customerName), safe(hallId), safe(hallName), safe(hallType),
                DateTimeUtil.formatDateTime(start), DateTimeUtil.formatDateTime(end),
                String.valueOf(totalAmount), status == null ? BookingStatus.PENDING.name() : status.name(),
                DateTimeUtil.formatDateTime(createdAt));
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getHallId() {
        return hallId;
    }

    public void setHallId(String hallId) {
        this.hallId = hallId;
    }

    public String getHallName() {
        return hallName;
    }

    public void setHallName(String hallName) {
        this.hallName = hallName;
    }

    public String getHallType() {
        return hallType;
    }

    public void setHallType(String hallType) {
        this.hallType = hallType;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public void setStart(LocalDateTime start) {
        this.start = start;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public void setEnd(LocalDateTime end) {
        this.end = end;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
