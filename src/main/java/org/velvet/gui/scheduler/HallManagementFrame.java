package org.velvet.gui.scheduler;

import org.velvet.AppContext;
import org.velvet.gui.GuiUtil;
import org.velvet.model.hall.Hall;
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
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.time.LocalDateTime;
import java.util.List;

public class HallManagementFrame extends JFrame {
    private final User scheduler;
    private final JTextField filterField;
    private final DefaultTableModel model;
    private final JTable table;

    public HallManagementFrame(User scheduler) {
        this.scheduler = scheduler;

        setTitle("Hall Management");
        setSize(980, 520);
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

        model = new DefaultTableModel(new Object[]{"ID", "Type", "Name", "Capacity", "Rate/Hr", "Available From", "Available To", "Remarks"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(model);
        root.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel actions = new JPanel();
        JButton addButton = new JButton("Add Hall");
        JButton editButton = new JButton("Edit Hall");
        JButton deleteButton = new JButton("Delete Hall");
        JButton availabilityButton = new JButton("Set Availability");
        JButton refreshButton = new JButton("Refresh");
        actions.add(addButton);
        actions.add(editButton);
        actions.add(deleteButton);
        actions.add(availabilityButton);
        actions.add(refreshButton);
        root.add(actions, BorderLayout.SOUTH);

        searchButton.addActionListener(e -> refreshTable());
        refreshButton.addActionListener(e -> {
            filterField.setText("");
            refreshTable();
        });
        addButton.addActionListener(e -> addHall());
        editButton.addActionListener(e -> editHall());
        deleteButton.addActionListener(e -> deleteHall());
        availabilityButton.addActionListener(e -> setAvailability());

        setContentPane(root);
        refreshTable();
    }

    private void refreshTable() {
        model.setRowCount(0);
        List<Hall> halls = AppContext.SCHEDULER_CONTROLLER.searchHalls(filterField.getText());
        for (Hall hall : halls) {
            model.addRow(new Object[]{
                    hall.getId(), hall.getType(), hall.getName(), hall.getCapacity(),
                    String.format("RM %.2f", hall.getRatePerHour()),
                    DateTimeUtil.formatDateTime(hall.getAvailableFrom()),
                    DateTimeUtil.formatDateTime(hall.getAvailableTo()),
                    hall.getRemarks()
            });
        }
    }

    private void addHall() {
        HallForm form = showHallForm(null);
        if (form == null) {
            return;
        }
        try {
            AppContext.SCHEDULER_CONTROLLER.addHall(form.type, form.name, form.from, form.to, form.remarks);
            refreshTable();
            GuiUtil.showInfo(this, "Hall added successfully.");
        } catch (Exception e) {
            GuiUtil.showError(this, e);
        }
    }

    private void editHall() {
        int row = table.getSelectedRow();
        if (row < 0) {
            GuiUtil.showInfo(this, "Select a hall first.");
            return;
        }

        Hall hall = AppContext.HALL_SERVICE.findHallById(model.getValueAt(row, 0).toString());
        HallForm form = showHallForm(hall);
        if (form == null) {
            return;
        }

        try {
            AppContext.SCHEDULER_CONTROLLER.updateHall(hall.getId(), form.type, form.name, form.from, form.to, form.remarks);
            refreshTable();
            GuiUtil.showInfo(this, "Hall updated successfully.");
        } catch (Exception e) {
            GuiUtil.showError(this, e);
        }
    }

    private void deleteHall() {
        int row = table.getSelectedRow();
        if (row < 0) {
            GuiUtil.showInfo(this, "Select a hall first.");
            return;
        }

        String hallId = model.getValueAt(row, 0).toString();
        int option = JOptionPane.showConfirmDialog(this, "Delete hall " + hallId + "?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (option != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            AppContext.SCHEDULER_CONTROLLER.deleteHall(hallId);
            refreshTable();
        } catch (Exception e) {
            GuiUtil.showError(this, e);
        }
    }

    private void setAvailability() {
        int row = table.getSelectedRow();
        if (row < 0) {
            GuiUtil.showInfo(this, "Select a hall first.");
            return;
        }

        String hallId = model.getValueAt(row, 0).toString();

        JTextField fromField = new JTextField(model.getValueAt(row, 5).toString());
        JTextField toField = new JTextField(model.getValueAt(row, 6).toString());
        JTextField remarksField = new JTextField(model.getValueAt(row, 7).toString());

        JPanel panel = new JPanel(new GridLayout(0, 2, 8, 8));
        panel.add(new JLabel("Available From (yyyy-MM-dd HH:mm):"));
        panel.add(fromField);
        panel.add(new JLabel("Available To (yyyy-MM-dd HH:mm):"));
        panel.add(toField);
        panel.add(new JLabel("Remarks:"));
        panel.add(remarksField);

        int option = JOptionPane.showConfirmDialog(this, panel, "Set Hall Availability", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (option != JOptionPane.OK_OPTION) {
            return;
        }

        try {
            LocalDateTime from = DateTimeUtil.parseDateTime(fromField.getText().trim());
            LocalDateTime to = DateTimeUtil.parseDateTime(toField.getText().trim());
            AppContext.SCHEDULER_CONTROLLER.setAvailability(hallId, from, to, remarksField.getText());
            refreshTable();
            GuiUtil.showInfo(this, "Availability updated.");
        } catch (Exception e) {
            GuiUtil.showError(this, e);
        }
    }

    private HallForm showHallForm(Hall hall) {
        JComboBox<String> typeBox = new JComboBox<>(new String[]{"Auditorium", "Banquet Hall", "Meeting Room"});
        JTextField nameField = new JTextField(hall == null ? "" : hall.getName());
        JTextField fromField = new JTextField(hall == null ? "" : DateTimeUtil.formatDateTime(hall.getAvailableFrom()));
        JTextField toField = new JTextField(hall == null ? "" : DateTimeUtil.formatDateTime(hall.getAvailableTo()));
        JTextField remarksField = new JTextField(hall == null ? "" : hall.getRemarks());

        if (hall != null) {
            typeBox.setSelectedItem(hall.getType());
        }

        JPanel panel = new JPanel(new GridLayout(0, 2, 8, 8));
        panel.add(new JLabel("Hall Type:"));
        panel.add(typeBox);
        panel.add(new JLabel("Hall Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Available From (yyyy-MM-dd HH:mm):"));
        panel.add(fromField);
        panel.add(new JLabel("Available To (yyyy-MM-dd HH:mm):"));
        panel.add(toField);
        panel.add(new JLabel("Remarks:"));
        panel.add(remarksField);

        int option = JOptionPane.showConfirmDialog(this, panel, hall == null ? "Add Hall" : "Edit Hall",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (option != JOptionPane.OK_OPTION) {
            return null;
        }

        HallForm form = new HallForm();
        form.type = typeBox.getSelectedItem().toString();
        form.name = nameField.getText().trim();
        form.from = DateTimeUtil.parseDateTime(fromField.getText().trim());
        form.to = DateTimeUtil.parseDateTime(toField.getText().trim());
        form.remarks = remarksField.getText().trim();
        return form;
    }

    private static class HallForm {
        private String type;
        private String name;
        private LocalDateTime from;
        private LocalDateTime to;
        private String remarks;
    }
}
