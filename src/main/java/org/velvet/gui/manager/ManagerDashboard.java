package org.velvet.gui.manager;

import org.velvet.gui.login.LoginFrame;
import org.velvet.model.user.User;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.GridLayout;

public class ManagerDashboard extends JFrame {
    private final User manager;

    public ManagerDashboard(User manager) {
        this.manager = manager;

        setTitle("Manager Dashboard - " + manager.getName());
        setSize(440, 270);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(new EmptyBorder(15, 15, 15, 15));

        root.add(new JLabel("Welcome, " + manager.getName()), BorderLayout.NORTH);

        JPanel actions = new JPanel(new GridLayout(0, 1, 10, 10));
        JButton salesButton = new JButton("Sales Dashboard");
        JButton issueButton = new JButton("Issue Management");
        JButton logoutButton = new JButton("Logout");
        actions.add(salesButton);
        actions.add(issueButton);
        actions.add(logoutButton);

        root.add(actions, BorderLayout.CENTER);

        salesButton.addActionListener(e -> new SalesDashboardFrame().setVisible(true));
        issueButton.addActionListener(e -> new IssueManagementFrame().setVisible(true));
        logoutButton.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true);
        });

        setContentPane(root);
    }
}
