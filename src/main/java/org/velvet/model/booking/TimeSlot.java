package org.velvet.model.booking;

import java.time.LocalDateTime;

public class TimeSlot {
    private final LocalDateTime start;
    private final LocalDateTime end;
    private final String remarks;

    public TimeSlot(LocalDateTime start, LocalDateTime end) {
        this(start, end, "");
    }

    public TimeSlot(LocalDateTime start, LocalDateTime end, String remarks) {
        this.start = start;
        this.end = end;
        this.remarks = remarks == null ? "" : remarks;
    }

    public boolean overlaps(TimeSlot other) {
        if (other == null) {
            return false;
        }
        return start.isBefore(other.end) && end.isAfter(other.start);
    }

    public boolean isValidSlot() {
        return start != null && end != null && end.isAfter(start);
    }

    public LocalDateTime getStart() {
        return start;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public String getRemarks() {
        return remarks;
    }
}
