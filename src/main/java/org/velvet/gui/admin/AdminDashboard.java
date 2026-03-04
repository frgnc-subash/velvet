package org.velvet.gui.admin;

import org.velvet.gui.login.LoginFrame;
import org.velvet.model.user.User;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.GridLayout;

public class AdminDashboard extends JFrame {
    private final User admin;

    public AdminDashboard(User admin) {
        this.admin = admin;

        setTitle("Administrator Dashboard - " + admin.getName());
        setSize(450, 280);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(new EmptyBorder(15, 15, 15, 15));

        root.add(new JLabel("Welcome, " + admin.getName()), BorderLayout.NORTH);

        JPanel actions = new JPanel(new GridLayout(0, 1, 10, 10));
        JButton userManagementButton = new JButton("Scheduler & User Management");
        JButton bookingOverviewButton = new JButton("Booking Management");
        JButton logoutButton = new JButton("Logout");

        actions.add(userManagementButton);
        actions.add(bookingOverviewButton);
        actions.add(logoutButton);
        root.add(actions, BorderLayout.CENTER);

        userManagementButton.addActionListener(e -> new UserManagementFrame().setVisible(true));
        bookingOverviewButton.addActionListener(e -> new BookingOverviewFrame().setVisible(true));
        logoutButton.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true);
        });

        setContentPane(root);
    }
}
