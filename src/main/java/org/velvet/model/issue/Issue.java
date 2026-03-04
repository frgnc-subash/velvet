package org.velvet.model.issue;

import org.velvet.util.DateTimeUtil;

import java.time.LocalDateTime;

public class Issue {
    private String id;
    private String bookingId;
    private String customerId;
    private String hallId;
    private String description;
    private IssueStatus status;
    private String assignedSchedulerId;
    private String managerResponse;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Issue() {
    }

    public Issue(String id, String bookingId, String customerId, String hallId, String description,
                 IssueStatus status, String assignedSchedulerId, String managerResponse,
                 LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.bookingId = bookingId;
        this.customerId = customerId;
        this.hallId = hallId;
        this.description = description;
        this.status = status;
        this.assignedSchedulerId = assignedSchedulerId;
        this.managerResponse = managerResponse;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String toRecord() {
        return String.join("|",
                safe(id), safe(bookingId), safe(customerId), safe(hallId), safe(description),
                status == null ? IssueStatus.IN_PROGRESS.name() : status.name(), safe(assignedSchedulerId),
                safe(managerResponse), DateTimeUtil.formatDateTime(createdAt), DateTimeUtil.formatDateTime(updatedAt));
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

    public String getHallId() {
        return hallId;
    }

    public void setHallId(String hallId) {
        this.hallId = hallId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public IssueStatus getStatus() {
        return status;
    }

    public void setStatus(IssueStatus status) {
        this.status = status;
    }

    public String getAssignedSchedulerId() {
        return assignedSchedulerId;
    }

    public void setAssignedSchedulerId(String assignedSchedulerId) {
        this.assignedSchedulerId = assignedSchedulerId;
    }

    public String getManagerResponse() {
        return managerResponse;
    }

    public void setManagerResponse(String managerResponse) {
        this.managerResponse = managerResponse;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
