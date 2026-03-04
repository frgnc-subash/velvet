package org.velvet.model.hall;

import java.time.LocalDateTime;

public class Auditorium extends Hall {
    public Auditorium(String id, String name, LocalDateTime availableFrom, LocalDateTime availableTo, String remarks) {
        super(id, name, "Auditorium", 1000, 300.0, availableFrom, availableTo, remarks);
    }
}
