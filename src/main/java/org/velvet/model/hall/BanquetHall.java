package org.velvet.model.hall;

import java.time.LocalDateTime;

public class BanquetHall extends Hall {
    public BanquetHall(String id, String name, LocalDateTime availableFrom, LocalDateTime availableTo, String remarks) {
        super(id, name, "Banquet Hall", 300, 100.0, availableFrom, availableTo, remarks);
    }
}
