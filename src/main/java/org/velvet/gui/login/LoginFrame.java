package org.velvet.gui.login;

import org.velvet.AppContext;
import org.velvet.exception.InvalidLoginException;
import org.velvet.gui.GuiUtil;
import org.velvet.gui.admin.AdminDashboard;
import org.velvet.gui.customer.CustomerDashboard;
import org.velvet.gui.manager.ManagerDashboard;
import org.velvet.gui.scheduler.SchedulerDashboard;
import org.velvet.model.user.Customer;
import org.velvet.model.user.User;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.GridLayout;

public class LoginFrame extends JFrame {
    private final JTextField usernameField;
    private final JPasswordField passwordField;

    public LoginFrame() {
        setTitle("Hall Booking Management System - Login");
        setSize(500, 330);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(new EmptyBorder(15, 15, 15, 15));

        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.add(new JLabel("Username:"));
        usernameField = new JTextField();
        form.add(usernameField);

        form.add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        form.add(passwordField);

        root.add(form, BorderLayout.CENTER);

        JLabel hint = new JLabel("Default: admin/admin123, scheduler/scheduler123, manager/manager123, customer/customer123");
        root.add(hint, BorderLayout.NORTH);

        JPanel actions = new JPanel();
        JButton loginButton = new JButton("Login");
        JButton registerButton = new JButton("Register Customer");
        actions.add(loginButton);
        actions.add(registerButton);
        root.add(actions, BorderLayout.SOUTH);

        loginButton.addActionListener(e -> performLogin());
        registerButton.addActionListener(e -> showRegisterDialog());

        // --- ADD THIS ONE LINE ---
        // This makes pressing "Enter" anywhere in the window trigger the loginButton
        getRootPane().setDefaultButton(loginButton);

        setContentPane(root);
    }
    private void performLogin() {
        try {
            User user = AppContext.LOGIN_CONTROLLER.login(usernameField.getText(), new String(passwordField.getPassword()));
            openDashboard(user);
            dispose();
        } catch (InvalidLoginException e) {
            GuiUtil.showError(this, e);
        }
    }private void showRegisterDialog() {
        // 1. Declare components outside the loop so they retain user input on error
        JTextField name = new JTextField();
        JTextField username = new JTextField();
        JPasswordField password = new JPasswordField();
        JTextField phone = new JTextField();
        JTextField email = new JTextField();

        JPanel panel = new JPanel(new GridLayout(0, 2, 8, 8));
        panel.add(new JLabel("Name:"));
        panel.add(name);
        panel.add(new JLabel("Username:"));
        panel.add(username);
        panel.add(new JLabel("Password:"));
        panel.add(password);
        panel.add(new JLabel("Phone:"));
        panel.add(phone);
        panel.add(new JLabel("Email:"));
        panel.add(email);

        // 2. Wrap the dialog in a loop
        while (true) {
            int option = JOptionPane.showConfirmDialog(this, panel, "Customer Registration",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            // If user clicks Cancel or the X button, exit the loop and method
            if (option != JOptionPane.OK_OPTION) {
                return;
            }

            try {
                Customer customer = AppContext.LOGIN_CONTROLLER.registerCustomer(
                        name.getText(),
                        username.getText(),
                        new String(password.getPassword()),
                        phone.getText(),
                        email.getText()
                );
                GuiUtil.showInfo(this, "Registration successful. Your customer ID is " + customer.getId());

                // 3. Break out of the loop because registration was successful
                break;

            } catch (Exception ex) {
                // Show the error. The loop will restart and show the dialog again.
                GuiUtil.showError(this, ex);
            }
        }
    }

    private void openDashboard(User user) {
        String role = user.getRole();
        if (User.ROLE_CUSTOMER.equalsIgnoreCase(role)) {
            new CustomerDashboard(user).setVisible(true);
        } else if (User.ROLE_SCHEDULER.equalsIgnoreCase(role)) {
            new SchedulerDashboard(user).setVisible(true);
        } else if (User.ROLE_ADMINISTRATOR.equalsIgnoreCase(role)) {
            new AdminDashboard(user).setVisible(true);
        } else if (User.ROLE_MANAGER.equalsIgnoreCase(role)) {
            new ManagerDashboard(user).setVisible(true);
        } else {
            GuiUtil.showInfo(this, "Unsupported role: " + role);
        }
    }
}
