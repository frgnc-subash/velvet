package org.velvet.gui.customer;

import org.velvet.AppContext;
import org.velvet.gui.GuiUtil;
import org.velvet.model.booking.Booking;
import org.velvet.model.issue.Issue;
import org.velvet.model.user.User;
import org.velvet.util.DateTimeUtil;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
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

public class IssueFrame extends JFrame {
    private final User customer;
    private final JTextField filterField;
    private final DefaultTableModel model;

    public IssueFrame(User customer) {
        this.customer = customer;

        setTitle("Issue Management - " + customer.getName());
        setSize(980, 520);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel top = new JPanel(new BorderLayout(8, 8));
        filterField = new JTextField();
        JButton searchButton = new JButton("Search");
        JButton raiseButton = new JButton("Raise New Issue");

        JPanel topLeft = new JPanel(new BorderLayout(8, 8));
        topLeft.add(new JLabel("Filter:"), BorderLayout.WEST);
        topLeft.add(filterField, BorderLayout.CENTER);
        top.add(topLeft, BorderLayout.CENTER);

        JPanel topRight = new JPanel();
        topRight.add(searchButton);
        topRight.add(raiseButton);
        top.add(topRight, BorderLayout.EAST);

        root.add(top, BorderLayout.NORTH);

        model = new DefaultTableModel(new Object[]{"Issue ID", "Booking ID", "Hall ID", "Description", "Status", "Assigned Scheduler", "Manager Response", "Created"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(model);
        root.add(new JScrollPane(table), BorderLayout.CENTER);

        searchButton.addActionListener(e -> refreshTable());
        raiseButton.addActionListener(e -> raiseIssue());

        setContentPane(root);
        refreshTable();
    }

    private void refreshTable() {
        model.setRowCount(0);
        String key = filterField.getText() == null ? "" : filterField.getText().trim().toLowerCase();
        List<Issue> issues = AppContext.CUSTOMER_CONTROLLER.getCustomerIssues(customer.getId());
        for (Issue issue : issues) {
            if (!key.isEmpty() && !issue.getId().toLowerCase().contains(key)
                    && !issue.getDescription().toLowerCase().contains(key)
                    && !issue.getStatus().name().toLowerCase().contains(key)) {
                continue;
            }
            model.addRow(new Object[]{
                    issue.getId(), issue.getBookingId(), issue.getHallId(), issue.getDescription(),
                    issue.getStatus().name(), issue.getAssignedSchedulerId(), issue.getManagerResponse(),
                    DateTimeUtil.formatDateTime(issue.getCreatedAt())
            });
        }
    }

    private void raiseIssue() {
        List<Booking> bookings = AppContext.CUSTOMER_CONTROLLER.getAllCustomerBookings(customer.getId());
        if (bookings.isEmpty()) {
            GuiUtil.showInfo(this, "You have no bookings yet.");
            return;
        }

        JComboBox<String> bookingBox = new JComboBox<>(
                bookings.stream()
                        .map(b -> b.getId() + " | " + b.getHallId() + " | " + DateTimeUtil.formatDateTime(b.getStart()))
                        .toArray(String[]::new));
        JTextArea descriptionArea = new JTextArea(4, 30);

        JPanel panel = new JPanel(new GridLayout(0, 1, 8, 8));
        panel.add(new JLabel("Booking:"));
        panel.add(bookingBox);
        panel.add(new JLabel("Issue Description:"));
        panel.add(new JScrollPane(descriptionArea));

        int option = javax.swing.JOptionPane.showConfirmDialog(this, panel, "Raise Issue",
                javax.swing.JOptionPane.OK_CANCEL_OPTION, javax.swing.JOptionPane.PLAIN_MESSAGE);
        if (option != javax.swing.JOptionPane.OK_OPTION) {
            return;
        }

        try {
            String[] parts = bookingBox.getSelectedItem().toString().split("\\|");
            String bookingId = parts[0].trim();
            String hallId = parts[1].trim();

            AppContext.CUSTOMER_CONTROLLER.raiseIssue(bookingId, customer.getId(), hallId, descriptionArea.getText());
            refreshTable();
            GuiUtil.showInfo(this, "Issue submitted successfully.");
        } catch (Exception e) {
            GuiUtil.showError(this, e);
        }
    }
}
