package org.velvet.model.service;

import org.velvet.exception.InvalidLoginException;
import org.velvet.model.user.Administrator;
import org.velvet.model.user.Customer;
import org.velvet.model.user.Manager;
import org.velvet.model.user.Scheduler;
import org.velvet.model.user.User;
import org.velvet.util.FileHandler;
import org.velvet.util.IdGenerator;
import org.velvet.util.ValidationUtil;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class UserService {
    private static final Path USERS_FILE = Path.of("src/main/resources/data/users.txt");

    public UserService() {
        FileHandler.ensureFile(USERS_FILE);
        seedDefaultsIfEmpty();
    }

    public User login(String username, String password) throws InvalidLoginException {
        ValidationUtil.requireNotBlank(username, "Username");
        ValidationUtil.requireNotBlank(password, "Password");

        return getAllUsers().stream()
                .filter(user -> username.equalsIgnoreCase(user.getUsername()))
                .findFirst()
                .map(user -> {
                    if (user.isBlocked()) {
                        throw new IllegalStateException("Your account has been blocked.");
                    }
                    if (!password.equals(user.getPassword())) {
                        throw new IllegalArgumentException("Invalid username or password.");
                    }
                    return user;
                })
                .orElseThrow(() -> new IllegalArgumentException("Invalid username or password."));
    }

    public Customer registerCustomer(String name, String username, String password, String phone, String email) {
        validateUserInputs(name, username, password, phone, email);
        if (isUsernameTaken(username, null)) {
            throw new IllegalArgumentException("Username already exists.");
        }

        Customer customer = new Customer(
                IdGenerator.generate("CUS"),
                name.trim(),
                username.trim(),
                password.trim(),
                phone.trim(),
                email.trim(),
                false
        );
        List<User> users = getAllUsers();
        users.add(customer);
        saveAll(users);
        return customer;
    }

    public User addUser(String name, String username, String password, String role, String phone, String email) {
        validateUserInputs(name, username, password, phone, email);
        ValidationUtil.requireNotBlank(role, "Role");

        if (isUsernameTaken(username, null)) {
            throw new IllegalArgumentException("Username already exists.");
        }

        User user = createUserByRole(role.trim().toUpperCase(Locale.ROOT));
        user.setId(IdGenerator.generate(role.trim().substring(0, Math.min(3, role.trim().length())).toUpperCase(Locale.ROOT)));
        user.setName(name.trim());
        user.setUsername(username.trim());
        user.setPassword(password.trim());
        user.setPhone(phone.trim());
        user.setEmail(email.trim());
        user.setBlocked(false);

        List<User> users = getAllUsers();
        users.add(user);
        saveAll(users);
        return user;
    }

    public void updateUser(String userId, String name, String username, String password, String phone, String email, boolean blocked) {
        ValidationUtil.requireNotBlank(userId, "User ID");
        validateUserInputs(name, username, password, phone, email);

        List<User> users = getAllUsers();
        User user = users.stream().filter(u -> u.getId().equals(userId)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("User not found."));

        if (isUsernameTaken(username, userId)) {
            throw new IllegalArgumentException("Username already exists.");
        }

        user.setName(name.trim());
        user.setUsername(username.trim());
        user.setPassword(password.trim());
        user.setPhone(phone.trim());
        user.setEmail(email.trim());
        user.setBlocked(blocked);
        saveAll(users);
    }

    public void updateProfile(String userId, String name, String phone, String email, String password) {
        ValidationUtil.requireNotBlank(userId, "User ID");
        ValidationUtil.requireNotBlank(name, "Name");
        ValidationUtil.validatePhone(phone);
        ValidationUtil.validateEmail(email);
        ValidationUtil.requireNoPipe(name, "Name");
        ValidationUtil.requireNoPipe(password, "Password");

        List<User> users = getAllUsers();
        User user = users.stream().filter(u -> u.getId().equals(userId)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("User not found."));

        user.setName(name.trim());
        user.setPhone(phone.trim());
        user.setEmail(email.trim());
        if (password != null && !password.trim().isEmpty()) {
            user.setPassword(password.trim());
        }
        saveAll(users);
    }

    public void setBlocked(String userId, boolean blocked) {
        List<User> users = getAllUsers();
        User user = users.stream().filter(u -> u.getId().equals(userId)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("User not found."));
        user.setBlocked(blocked);
        saveAll(users);
    }

    public void deleteUser(String userId) {
        List<User> users = getAllUsers();
        boolean removed = users.removeIf(user -> user.getId().equals(userId));
        if (!removed) {
            throw new IllegalArgumentException("User not found.");
        }
        saveAll(users);
    }

    public User findById(String userId) {
        return getAllUsers().stream().filter(user -> user.getId().equals(userId)).findFirst().orElse(null);
    }

    public List<User> getAllUsers() {
        List<String> lines = FileHandler.readAllLines(USERS_FILE);
        List<User> users = new ArrayList<>();
        for (String line : lines) {
            if (line == null || line.isBlank()) {
                continue;
            }
            users.add(parseUser(line));
        }
        return users;
    }

    public List<User> searchUsers(String keyword) {
        String key = keyword == null ? "" : keyword.trim().toLowerCase(Locale.ROOT);
        return getAllUsers().stream()
                .filter(user -> key.isEmpty() ||
                        user.getId().toLowerCase(Locale.ROOT).contains(key) ||
                        user.getName().toLowerCase(Locale.ROOT).contains(key) ||
                        user.getUsername().toLowerCase(Locale.ROOT).contains(key) ||
                        user.getRole().toLowerCase(Locale.ROOT).contains(key))
                .collect(Collectors.toList());
    }

    public List<User> getUsersByRole(String role) {
        String normalized = role == null ? "" : role.trim().toUpperCase(Locale.ROOT);
        return getAllUsers().stream()
                .filter(user -> normalized.isEmpty() || user.getRole().equalsIgnoreCase(normalized))
                .collect(Collectors.toList());
    }

    private void validateUserInputs(String name, String username, String password, String phone, String email) {
        ValidationUtil.requireNotBlank(name, "Name");
        ValidationUtil.requireNotBlank(username, "Username");
        ValidationUtil.validatePassword(password);
        ValidationUtil.validatePhone(phone);
        ValidationUtil.validateEmail(email);
        ValidationUtil.requireNoPipe(name, "Name");
        ValidationUtil.requireNoPipe(username, "Username");
        ValidationUtil.requireNoPipe(password, "Password");
    }

    private boolean isUsernameTaken(String username, String exceptUserId) {
        return getAllUsers().stream()
                .anyMatch(user -> user.getUsername().equalsIgnoreCase(username.trim()) &&
                        (exceptUserId == null || !user.getId().equals(exceptUserId)));
    }

    private void saveAll(List<User> users) {
        List<String> lines = users.stream().map(User::toRecord).collect(Collectors.toList());
        FileHandler.writeAllLines(USERS_FILE, lines);
    }

    private User parseUser(String line) {
        String[] parts = line.split("\\|", -1);
        if (parts.length < 8) {
            throw new IllegalArgumentException("Corrupted user record: " + line);
        }
        String role = parts[4].trim().toUpperCase(Locale.ROOT);
        User user = createUserByRole(role);
        user.setId(parts[0]);
        user.setName(parts[1]);
        user.setUsername(parts[2]);
        user.setPassword(parts[3]);
        user.setRole(role);
        user.setPhone(parts[5]);
        user.setEmail(parts[6]);
        user.setBlocked(Boolean.parseBoolean(parts[7]));
        return user;
    }

    private User createUserByRole(String role) {
        return switch (role) {
            case User.ROLE_CUSTOMER -> new Customer();
            case User.ROLE_SCHEDULER -> new Scheduler();
            case User.ROLE_ADMINISTRATOR -> new Administrator();
            case User.ROLE_MANAGER -> new Manager();
            default -> throw new IllegalArgumentException("Unsupported role: " + role);
        };
    }

    private void seedDefaultsIfEmpty() {
        if (!FileHandler.readAllLines(USERS_FILE).isEmpty()) {
            return;
        }
        List<User> defaults = new ArrayList<>();
        defaults.add(new Administrator("ADM-001", "System Admin", "admin", "admin123", "+60110000001", "admin@velvet.com", false));
        defaults.add(new Scheduler("SCH-001", "Main Scheduler", "scheduler", "scheduler123", "+60110000002", "scheduler@velvet.com", false));
        defaults.add(new Manager("MGR-001", "Operations Manager", "manager", "manager123", "+60110000003", "manager@velvet.com", false));
        defaults.add(new Customer("CUS-001", "Sample Customer", "customer", "customer123", "+60110000004", "customer@velvet.com", false));
        saveAll(defaults);
    }
}
