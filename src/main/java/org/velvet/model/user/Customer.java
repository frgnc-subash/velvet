package org.velvet.model.user;

public class Customer extends User {
    public Customer() {
        setRole(ROLE_CUSTOMER);
    }

    public Customer(String id, String name, String username, String password, String phone, String email, boolean blocked) {
        super(id, name, username, password, ROLE_CUSTOMER, phone, email, blocked);
    }
}
