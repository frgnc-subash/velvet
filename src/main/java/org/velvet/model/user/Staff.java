package org.velvet.model.user;

public abstract class Staff extends User {
    protected Staff() {
    }

    protected Staff(String id, String name, String username, String password, String role, String phone, String email, boolean blocked) {
        super(id, name, username, password, role, phone, email, blocked);
    }
}
