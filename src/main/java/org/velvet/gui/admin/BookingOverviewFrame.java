package org.velvet.gui.admin;

import org.velvet.AppContext;
import org.velvet.model.booking.Booking;
import org.velvet.util.DateTimeUtil;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.util.List;

public class BookingOverviewFrame extends JFrame {
    private final JComboBox<String> filterType;
    private final JTextField keywordField;
    private final DefaultTableModel model;

    public BookingOverviewFrame() {
        setTitle("Booking Management - Administrator");
        setSize(1040, 620);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel top = new JPanel(new BorderLayout(8, 8));
        filterType = new JComboBox<>(new String[]{"ALL", "UPCOMING", "PAST"});
        keywordField = new JTextField();
        JButton searchButton = new JButton("Search");

        JPanel left = new JPanel();
        left.add(new JLabel("Type:"));
        left.add(filterType);
        top.add(left, BorderLayout.WEST);
        top.add(keywordField, BorderLayout.CENTER);
        top.add(searchButton, BorderLayout.EAST);

        root.add(top, BorderLayout.NORTH);

        model = new DefaultTableModel(new Object[]{"Booking ID", "Customer", "Hall", "Start", "End", "Amount", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(model);
        root.add(new JScrollPane(table), BorderLayout.CENTER);

        searchButton.addActionListener(e -> refresh());

        setContentPane(root);
        refresh();
    }

    private void refresh() {
        model.setRowCount(0);
        List<Booking> bookings = AppContext.ADMIN_CONTROLLER.filterBookings(
                filterType.getSelectedItem().toString(),
                keywordField.getText()
        );

        for (Booking booking : bookings) {
            model.addRow(new Object[]{
                    booking.getId(),
                    booking.getCustomerName(),
                    booking.getHallName() + " (" + booking.getHallType() + ")",
                    DateTimeUtil.formatDateTime(booking.getStart()),
                    DateTimeUtil.formatDateTime(booking.getEnd()),
                    String.format("RM %.2f", booking.getTotalAmount()),
                    booking.getStatus().name()
            });
        }
    }
}
