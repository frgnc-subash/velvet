package org.velvet.model.issue;

import org.velvet.util.DateTimeUtil;

import java.time.LocalDateTime;

public class MaintenanceTask {
    private String id;
    private String hallId;
    private String hallName;
    private LocalDateTime start;
    private LocalDateTime end;
    private String remarks;
    private String schedulerId;

    public MaintenanceTask() {
    }

    public MaintenanceTask(String id, String hallId, String hallName, LocalDateTime start,
                            LocalDateTime end, String remarks, String schedulerId) {
        this.id = id;
        this.hallId = hallId;
        this.hallName = hallName;
        this.start = start;
        this.end = end;
        this.remarks = remarks;
        this.schedulerId = schedulerId;
    }

    public String toRecord() {
        return String.join("|", safe(id), safe(hallId), safe(hallName),
                DateTimeUtil.formatDateTime(start), DateTimeUtil.formatDateTime(end), safe(remarks), safe(schedulerId));
    }

    public boolean overlaps(LocalDateTime otherStart, LocalDateTime otherEnd) {
        return start.isBefore(otherEnd) && end.isAfter(otherStart);
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

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getSchedulerId() {
        return schedulerId;
    }

    public void setSchedulerId(String schedulerId) {
        this.schedulerId = schedulerId;
    }
}
