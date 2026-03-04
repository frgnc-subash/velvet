package org.velvet.model.user;

public class Manager extends Staff {
    public Manager() {
        setRole(ROLE_MANAGER);
    }

    public Manager(String id, String name, String username, String password, String phone, String email, boolean blocked) {
        super(id, name, username, password, ROLE_MANAGER, phone, email, blocked);
    }
}
