package org.velvet.model.user;

public class Scheduler extends Staff {
    public Scheduler() {
        setRole(ROLE_SCHEDULER);
    }

    public Scheduler(String id, String name, String username, String password, String phone, String email, boolean blocked) {
        super(id, name, username, password, ROLE_SCHEDULER, phone, email, blocked);
    }
}
