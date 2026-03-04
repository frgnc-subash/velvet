package org.velvet.gui.scheduler;

import org.velvet.gui.login.LoginFrame;
import org.velvet.model.user.User;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.GridLayout;

public class SchedulerDashboard extends JFrame {
    private final User scheduler;

    public SchedulerDashboard(User scheduler) {
        this.scheduler = scheduler;

        setTitle("Scheduler Dashboard - " + scheduler.getName());
        setSize(420, 260);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(new EmptyBorder(15, 15, 15, 15));
        root.add(new JLabel("Welcome, " + scheduler.getName()), BorderLayout.NORTH);

        JPanel actions = new JPanel(new GridLayout(0, 1, 10, 10));
        JButton hallButton = new JButton("Hall Management");
        JButton maintenanceButton = new JButton("Maintenance Schedule");
        JButton logoutButton = new JButton("Logout");
        actions.add(hallButton);
        actions.add(maintenanceButton);
        actions.add(logoutButton);
        root.add(actions, BorderLayout.CENTER);

        hallButton.addActionListener(e -> new HallManagementFrame(scheduler).setVisible(true));
        maintenanceButton.addActionListener(e -> new MaintenanceFrame(scheduler).setVisible(true));
        logoutButton.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true);
        });

        setContentPane(root);
    }
}
