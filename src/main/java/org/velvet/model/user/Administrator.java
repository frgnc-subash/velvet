package org.velvet.model.user;

public class Administrator extends Staff {
    public Administrator() {
        setRole(ROLE_ADMINISTRATOR);
    }

    public Administrator(String id, String name, String username, String password, String phone, String email, boolean blocked) {
        super(id, name, username, password, ROLE_ADMINISTRATOR, phone, email, blocked);
    }
}
