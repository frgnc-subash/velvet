package org.velvet.gui.manager;

import org.velvet.AppContext;
import org.velvet.gui.GuiUtil;
import org.velvet.model.booking.Booking;
import org.velvet.util.DateTimeUtil;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.util.List;

public class SalesDashboardFrame extends JFrame {
    private final JLabel weeklyLabel;
    private final JLabel monthlyLabel;
    private final JLabel yearlyLabel;

    private final JTextField fromDate;
    private final JTextField toDate;
    private final DefaultTableModel model;

    public SalesDashboardFrame() {
        setTitle("Sales Dashboard");
        setSize(1040, 620);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel summary = new JPanel(new GridLayout(1, 3, 10, 10));
        weeklyLabel = new JLabel();
        monthlyLabel = new JLabel();
        yearlyLabel = new JLabel();
        summary.add(weeklyLabel);
        summary.add(monthlyLabel);
        summary.add(yearlyLabel);
        root.add(summary, BorderLayout.NORTH);

        model = new DefaultTableModel(new Object[]{"Booking ID", "Customer", "Hall", "Date/Time", "Amount"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(model);
        root.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel filterPanel = new JPanel();
        fromDate = new JTextField(10);
        toDate = new JTextField(10);
        JButton loadButton = new JButton("Load Range");
        JButton refreshSummary = new JButton("Refresh Summary");

        filterPanel.add(new JLabel("From (yyyy-MM-dd):"));
        filterPanel.add(fromDate);
        filterPanel.add(new JLabel("To (yyyy-MM-dd):"));
        filterPanel.add(toDate);
        filterPanel.add(loadButton);
        filterPanel.add(refreshSummary);

        root.add(filterPanel, BorderLayout.SOUTH);

        loadButton.addActionListener(e -> loadRange());
        refreshSummary.addActionListener(e -> refreshSummary());

        setContentPane(root);
        refreshSummary();
    }

    private void refreshSummary() {
        weeklyLabel.setText(String.format("Weekly Sales: RM %.2f", AppContext.MANAGER_CONTROLLER.getWeeklySales()));
        monthlyLabel.setText(String.format("Monthly Sales: RM %.2f", AppContext.MANAGER_CONTROLLER.getMonthlySales()));
        yearlyLabel.setText(String.format("Yearly Sales: RM %.2f", AppContext.MANAGER_CONTROLLER.getYearlySales()));
    }

    private void loadRange() {
        model.setRowCount(0);
        try {
            LocalDate from = LocalDate.parse(fromDate.getText().trim(), DateTimeUtil.DATE_FORMATTER);
            LocalDate to = LocalDate.parse(toDate.getText().trim(), DateTimeUtil.DATE_FORMATTER);
            if (to.isBefore(from)) {
                throw new IllegalArgumentException("To date must be on or after from date.");
            }

            List<Booking> bookings = AppContext.MANAGER_CONTROLLER.getSalesBookings(from, to);
            for (Booking booking : bookings) {
                model.addRow(new Object[]{
                        booking.getId(),
                        booking.getCustomerName(),
                        booking.getHallName() + " (" + booking.getHallType() + ")",
                        DateTimeUtil.formatDateTime(booking.getStart()),
                        String.format("RM %.2f", booking.getTotalAmount())
                });
            }
        } catch (Exception e) {
            GuiUtil.showError(this, e);
        }
    }
}
