package org.velvet.model.payment;

import org.velvet.util.DateTimeUtil;

import java.time.LocalDateTime;

public class Payment {
    private String id;
    private String bookingId;
    private String customerId;
    private double amount;
    private String method;
    private String status;
    private LocalDateTime paidAt;

    public Payment() {
    }

    public Payment(String id, String bookingId, String customerId, double amount, String method, String status, LocalDateTime paidAt) {
        this.id = id;
        this.bookingId = bookingId;
        this.customerId = customerId;
        this.amount = amount;
        this.method = method;
        this.status = status;
        this.paidAt = paidAt;
    }

    public String toRecord() {
        return String.join("|", safe(id), safe(bookingId), safe(customerId), String.valueOf(amount),
                safe(method), safe(status), DateTimeUtil.formatDateTime(paidAt));
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

    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getPaidAt() {
        return paidAt;
    }

    public void setPaidAt(LocalDateTime paidAt) {
        this.paidAt = paidAt;
    }
}
