package org.velvet.gui.customer;

import org.velvet.AppContext;
import org.velvet.exception.BookingNotAllowedException;
import org.velvet.gui.GuiUtil;
import org.velvet.model.booking.Booking;
import org.velvet.model.hall.Hall;
import org.velvet.model.user.Customer;
import org.velvet.model.user.User;
import org.velvet.util.DateTimeUtil;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.time.LocalDateTime;
import java.util.List;

public class BookingFrame extends JFrame {
    private final Customer customer;

    private final JTextField hallFilterField;
    private final DefaultTableModel hallModel;
    private final JTable hallTable;

    private final JTextField startField;
    private final JTextField endField;

    private final JComboBox<String> bookingFilter;
    private final DefaultTableModel bookingModel;
    private final JTable bookingTable;

    public BookingFrame(User customerUser) {
        this.customer = (Customer) customerUser;

        setTitle("Booking & Payment - " + customer.getName());
        setSize(1120, 680);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel topPanel = new JPanel(new BorderLayout(8, 8));
        JPanel filterPanel = new JPanel(new BorderLayout(8, 8));
        hallFilterField = new JTextField();
        JButton searchHallButton = new JButton("Search Halls");
        filterPanel.add(new JLabel("Hall Filter:"), BorderLayout.WEST);
        filterPanel.add(hallFilterField, BorderLayout.CENTER);
        filterPanel.add(searchHallButton, BorderLayout.EAST);
        topPanel.add(filterPanel, BorderLayout.NORTH);

        hallModel = new DefaultTableModel(new Object[]{"Hall ID", "Type", "Name", "Capacity", "Rate/Hr", "Available From", "Available To", "Remarks"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        hallTable = new JTable(hallModel);
        topPanel.add(new JScrollPane(hallTable), BorderLayout.CENTER);

        JPanel bookingForm = new JPanel(new GridLayout(2, 4, 8, 8));
        startField = new JTextField();
        endField = new JTextField();
        JButton bookButton = new JButton("Book Selected Hall");

        bookingForm.add(new JLabel("Start (yyyy-MM-dd HH:mm):"));
        bookingForm.add(startField);
        bookingForm.add(new JLabel("End (yyyy-MM-dd HH:mm):"));
        bookingForm.add(endField);
        bookingForm.add(new JLabel());
        bookingForm.add(bookButton);
        bookingForm.add(new JLabel("Operating Hours: 08:00 - 18:00"));
        bookingForm.add(new JLabel());

        topPanel.add(bookingForm, BorderLayout.SOUTH);

        JPanel bottomPanel = new JPanel(new BorderLayout(8, 8));
        JPanel bookingTop = new JPanel(new BorderLayout(8, 8));
        bookingFilter = new JComboBox<>(new String[]{"ALL", "UPCOMING", "PAST"});
        JButton loadBookingsButton = new JButton("Load Bookings");
        JButton cancelBookingButton = new JButton("Cancel Selected Booking");
        JPanel bookingActionPanel = new JPanel();
        bookingActionPanel.add(new JLabel("Filter:"));
        bookingActionPanel.add(bookingFilter);
        bookingActionPanel.add(loadBookingsButton);
        bookingActionPanel.add(cancelBookingButton);
        bookingTop.add(bookingActionPanel, BorderLayout.WEST);
        bottomPanel.add(bookingTop, BorderLayout.NORTH);

        bookingModel = new DefaultTableModel(new Object[]{"Booking ID", "Hall", "Start", "End", "Amount (RM)", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        bookingTable = new JTable(bookingModel);
        bottomPanel.add(new JScrollPane(bookingTable), BorderLayout.CENTER);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, topPanel, bottomPanel);
        splitPane.setResizeWeight(0.52);
        root.add(splitPane, BorderLayout.CENTER);

        searchHallButton.addActionListener(e -> loadHalls());
        bookButton.addActionListener(e -> createBookingAndPay());
        loadBookingsButton.addActionListener(e -> loadBookings());
        cancelBookingButton.addActionListener(e -> cancelBooking());

        setContentPane(root);
        loadHalls();
        loadBookings();
    }

    private void loadHalls() {
        hallModel.setRowCount(0);
        List<Hall> halls = AppContext.CUSTOMER_CONTROLLER.searchHalls(hallFilterField.getText());
        for (Hall hall : halls) {
            hallModel.addRow(new Object[]{
                    hall.getId(), hall.getType(), hall.getName(), hall.getCapacity(), String.format("%.2f", hall.getRatePerHour()),
                    DateTimeUtil.formatDateTime(hall.getAvailableFrom()), DateTimeUtil.formatDateTime(hall.getAvailableTo()), hall.getRemarks()
            });
        }
    }

    private void loadBookings() {
        bookingModel.setRowCount(0);
        String selected = bookingFilter.getSelectedItem().toString();
        List<Booking> bookings;

        if ("UPCOMING".equals(selected)) {
            bookings = AppContext.CUSTOMER_CONTROLLER.getUpcomingCustomerBookings(customer.getId());
        } else if ("PAST".equals(selected)) {
            bookings = AppContext.CUSTOMER_CONTROLLER.getPastCustomerBookings(customer.getId());
        } else {
            bookings = AppContext.CUSTOMER_CONTROLLER.getAllCustomerBookings(customer.getId());
        }

        for (Booking booking : bookings) {
            bookingModel.addRow(new Object[]{
                    booking.getId(),
                    booking.getHallName() + " (" + booking.getHallType() + ")",
                    DateTimeUtil.formatDateTime(booking.getStart()),
                    DateTimeUtil.formatDateTime(booking.getEnd()),
                    String.format("%.2f", booking.getTotalAmount()),
                    booking.getStatus().name()
            });
        }
    }

    private void createBookingAndPay() {
        int row = hallTable.getSelectedRow();
        if (row < 0) {
            GuiUtil.showInfo(this, "Select a hall first.");
            return;
        }

        try {
            String hallId = hallModel.getValueAt(row, 0).toString();
            LocalDateTime start = DateTimeUtil.parseDateTime(startField.getText().trim());
            LocalDateTime end = DateTimeUtil.parseDateTime(endField.getText().trim());

            Booking booking = AppContext.CUSTOMER_CONTROLLER.createBooking(customer, hallId, start, end);
            new PaymentFrame(booking, this::loadBookings).setVisible(true);
            GuiUtil.showInfo(this, "Booking created. Proceed with payment.");
        } catch (Exception e) {
            GuiUtil.showError(this, e);
        }
    }

    private void cancelBooking() {
        int row = bookingTable.getSelectedRow();
        if (row < 0) {
            GuiUtil.showInfo(this, "Select a booking first.");
            return;
        }

        String bookingId = bookingModel.getValueAt(row, 0).toString();
        try {
            AppContext.CUSTOMER_CONTROLLER.cancelBooking(bookingId, customer.getId());
            loadBookings();
            GuiUtil.showInfo(this, "Booking cancelled.");
        } catch (BookingNotAllowedException e) {
            GuiUtil.showError(this, e);
        }
    }
}
