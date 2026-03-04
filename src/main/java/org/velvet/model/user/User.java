package org.velvet.model.user;

public abstract class User {
    public static final String ROLE_CUSTOMER = "CUSTOMER";
    public static final String ROLE_SCHEDULER = "SCHEDULER";
    public static final String ROLE_ADMINISTRATOR = "ADMINISTRATOR";
    public static final String ROLE_MANAGER = "MANAGER";

    private String id;
    private String name;
    private String username;
    private String password;
    private String role;
    private String phone;
    private String email;
    private boolean blocked;

    protected User() {
    }

    protected User(String id, String name, String username, String password, String role, String phone, String email, boolean blocked) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.password = password;
        this.role = role;
        this.phone = phone;
        this.email = email;
        this.blocked = blocked;
    }

    public String toRecord() {
        return String.join("|",
                safe(id), safe(name), safe(username), safe(password), safe(role),
                safe(phone), safe(email), String.valueOf(blocked));
    }

    protected String safe(String value) {
        return value == null ? "" : value;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    @Override
    public String toString() {
        return name + " (" + role + ")";
    }
}
