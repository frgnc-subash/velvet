package org.velvet.gui.customer;

import org.velvet.AppContext;
import org.velvet.gui.GuiUtil;
import org.velvet.gui.login.LoginFrame;
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

public class CustomerDashboard extends JFrame {
    private final User customer;

    public CustomerDashboard(User customer) {
        this.customer = customer;

        setTitle("Customer Dashboard - " + customer.getName());
        setSize(460, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(new EmptyBorder(15, 15, 15, 15));
        root.add(new JLabel("Welcome, " + customer.getName()), BorderLayout.NORTH);

        JPanel actions = new JPanel(new GridLayout(0, 1, 10, 10));
        JButton profileButton = new JButton("Update Profile");
        JButton bookingButton = new JButton("Booking & Payment");
        JButton issueButton = new JButton("Raise/View Issues");
        JButton logoutButton = new JButton("Logout");
        actions.add(profileButton);
        actions.add(bookingButton);
        actions.add(issueButton);
        actions.add(logoutButton);
        root.add(actions, BorderLayout.CENTER);

        profileButton.addActionListener(e -> updateProfile());
        bookingButton.addActionListener(e -> new BookingFrame(customer).setVisible(true));
        issueButton.addActionListener(e -> new IssueFrame(customer).setVisible(true));
        logoutButton.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true);
        });

        setContentPane(root);
    }

    private void updateProfile() {
        JTextField name = new JTextField(customer.getName());
        JTextField phone = new JTextField(customer.getPhone());
        JTextField email = new JTextField(customer.getEmail());
        JPasswordField password = new JPasswordField();

        JPanel panel = new JPanel(new GridLayout(0, 2, 8, 8));
        panel.add(new JLabel("Name:"));
        panel.add(name);
        panel.add(new JLabel("Phone:"));
        panel.add(phone);
        panel.add(new JLabel("Email:"));
        panel.add(email);
        panel.add(new JLabel("New Password (optional):"));
        panel.add(password);

        int option = JOptionPane.showConfirmDialog(this, panel, "Update Profile",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (option != JOptionPane.OK_OPTION) {
            return;
        }

        try {
            String passwordText = new String(password.getPassword());
            AppContext.CUSTOMER_CONTROLLER.updateProfile(customer.getId(), name.getText(), phone.getText(), email.getText(), passwordText);
            customer.setName(name.getText().trim());
            customer.setPhone(phone.getText().trim());
            customer.setEmail(email.getText().trim());
            if (!passwordText.isBlank()) {
                customer.setPassword(passwordText.trim());
            }
            GuiUtil.showInfo(this, "Profile updated successfully.");
        } catch (Exception ex) {
            GuiUtil.showError(this, ex);
        }
    }
}
