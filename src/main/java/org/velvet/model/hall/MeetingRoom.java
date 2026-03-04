package org.velvet.model.hall;

import java.time.LocalDateTime;

public class MeetingRoom extends Hall {
    public MeetingRoom(String id, String name, LocalDateTime availableFrom, LocalDateTime availableTo, String remarks) {
        super(id, name, "Meeting Room", 30, 50.0, availableFrom, availableTo, remarks);
    }
}
