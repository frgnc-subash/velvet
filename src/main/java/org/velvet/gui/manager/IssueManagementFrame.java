package org.velvet.gui.manager;

import org.velvet.AppContext;
import org.velvet.gui.GuiUtil;
import org.velvet.model.issue.Issue;
import org.velvet.model.issue.IssueStatus;
import org.velvet.model.user.User;
import org.velvet.util.DateTimeUtil;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.List;

public class IssueManagementFrame extends JFrame {
    private final JTextField filterField;
    private final DefaultTableModel model;
    private final JTable table;

    public IssueManagementFrame() {
        setTitle("Issue Management - Manager");
        setSize(1140, 620);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel top = new JPanel(new BorderLayout(8, 8));
        filterField = new JTextField();
        JButton searchButton = new JButton("Search");
        top.add(new JLabel("Filter:"), BorderLayout.WEST);
        top.add(filterField, BorderLayout.CENTER);
        top.add(searchButton, BorderLayout.EAST);
        root.add(top, BorderLayout.NORTH);

        model = new DefaultTableModel(new Object[]{"Issue ID", "Booking ID", "Customer", "Hall", "Description", "Status", "Assigned Scheduler", "Response", "Updated"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(model);
        root.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel actions = new JPanel();
        JButton assignButton = new JButton("Assign Scheduler");
        JButton statusButton = new JButton("Update Status");
        JButton refreshButton = new JButton("Refresh");
        actions.add(assignButton);
        actions.add(statusButton);
        actions.add(refreshButton);
        root.add(actions, BorderLayout.SOUTH);

        searchButton.addActionListener(e -> refreshTable());
        refreshButton.addActionListener(e -> {
            filterField.setText("");
            refreshTable();
        });

        assignButton.addActionListener(e -> assignScheduler());
        statusButton.addActionListener(e -> updateStatus());

        setContentPane(root);
        refreshTable();
    }

    private void refreshTable() {
        model.setRowCount(0);
        List<Issue> issues = AppContext.MANAGER_CONTROLLER.searchIssues(filterField.getText());
        for (Issue issue : issues) {
            model.addRow(new Object[]{
                    issue.getId(), issue.getBookingId(), issue.getCustomerId(), issue.getHallId(), issue.getDescription(),
                    issue.getStatus().name(), issue.getAssignedSchedulerId(), issue.getManagerResponse(), DateTimeUtil.formatDateTime(issue.getUpdatedAt())
            });
        }
    }

    private void assignScheduler() {
        int row = table.getSelectedRow();
        if (row < 0) {
            GuiUtil.showInfo(this, "Select an issue first.");
            return;
        }

        List<User> schedulers = AppContext.MANAGER_CONTROLLER.getSchedulers();
        if (schedulers.isEmpty()) {
            GuiUtil.showInfo(this, "No scheduler accounts found.");
            return;
        }

        JComboBox<String> schedulerBox = new JComboBox<>(schedulers.stream()
                .map(s -> s.getId() + " - " + s.getName())
                .toArray(String[]::new));
        JTextArea responseArea = new JTextArea(3, 30);

        JPanel panel = new JPanel(new GridLayout(0, 1, 8, 8));
        panel.add(new JLabel("Scheduler:"));
        panel.add(schedulerBox);
        panel.add(new JLabel("Manager Response:"));
        panel.add(new JScrollPane(responseArea));

        int option = JOptionPane.showConfirmDialog(this, panel, "Assign Scheduler",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (option != JOptionPane.OK_OPTION) {
            return;
        }

        String issueId = model.getValueAt(row, 0).toString();
        String schedulerId = schedulerBox.getSelectedItem().toString().split(" - ")[0].trim();
        try {
            AppContext.MANAGER_CONTROLLER.assignScheduler(issueId, schedulerId, responseArea.getText());
            refreshTable();
        } catch (Exception e) {
            GuiUtil.showError(this, e);
        }
    }

    private void updateStatus() {
        int row = table.getSelectedRow();
        if (row < 0) {
            GuiUtil.showInfo(this, "Select an issue first.");
            return;
        }

        JComboBox<IssueStatus> statusBox = new JComboBox<>(new IssueStatus[]{
                IssueStatus.IN_PROGRESS,
                IssueStatus.DONE,
                IssueStatus.CLOSED,
                IssueStatus.CANCELLED
        });
        JTextArea responseArea = new JTextArea(3, 30);

        JPanel panel = new JPanel(new GridLayout(0, 1, 8, 8));
        panel.add(new JLabel("Status:"));
        panel.add(statusBox);
        panel.add(new JLabel("Manager Response:"));
        panel.add(new JScrollPane(responseArea));

        int option = JOptionPane.showConfirmDialog(this, panel, "Update Issue Status",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (option != JOptionPane.OK_OPTION) {
            return;
        }

        String issueId = model.getValueAt(row, 0).toString();
        try {
            AppContext.MANAGER_CONTROLLER.updateIssueStatus(issueId, (IssueStatus) statusBox.getSelectedItem(), responseArea.getText());
            refreshTable();
        } catch (Exception e) {
            GuiUtil.showError(this, e);
        }
    }
}
