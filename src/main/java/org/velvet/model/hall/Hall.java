package org.velvet.model.hall;

import org.velvet.model.booking.TimeSlot;
import org.velvet.util.DateTimeUtil;

import java.time.LocalDateTime;

public abstract class Hall {
    private String id;
    private String name;
    private String type;
    private int capacity;
    private double ratePerHour;
    private String status;
    private LocalDateTime availableFrom;
    private LocalDateTime availableTo;
    private String remarks;

    protected Hall() {
    }

    protected Hall(String id, String name, String type, int capacity, double ratePerHour,
                   LocalDateTime availableFrom, LocalDateTime availableTo, String remarks) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.capacity = capacity;
        this.ratePerHour = ratePerHour;
        this.status = "ACTIVE";
        this.availableFrom = availableFrom;
        this.availableTo = availableTo;
        this.remarks = remarks;
    }

    public boolean isWithinAvailability(TimeSlot slot) {
        if (availableFrom == null || availableTo == null || slot == null) {
            return false;
        }
        return !slot.getStart().isBefore(availableFrom) && !slot.getEnd().isAfter(availableTo);
    }

    public String toRecord() {
        return String.join("|",
                safe(id), safe(type), safe(name), String.valueOf(capacity), String.valueOf(ratePerHour),
                DateTimeUtil.formatDateTime(availableFrom), DateTimeUtil.formatDateTime(availableTo), safe(remarks), safe(status));
    }

    public double calculateCost(double durationHours) {
        return Math.max(0, durationHours) * ratePerHour;
    }

    public void updateStatus(String status) {
        this.status = status == null || status.isBlank() ? "ACTIVE" : status.trim().toUpperCase();
    }

    public String getHallDetails() {
        return id + " | " + type + " | " + name + " | cap=" + capacity + " | RM" + String.format("%.2f", ratePerHour) + "/hr";
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public double getRatePerHour() {
        return ratePerHour;
    }

    public void setRatePerHour(double ratePerHour) {
        this.ratePerHour = ratePerHour;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getAvailableFrom() {
        return availableFrom;
    }

    public void setAvailableFrom(LocalDateTime availableFrom) {
        this.availableFrom = availableFrom;
    }

    public LocalDateTime getAvailableTo() {
        return availableTo;
    }

    public void setAvailableTo(LocalDateTime availableTo) {
        this.availableTo = availableTo;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
