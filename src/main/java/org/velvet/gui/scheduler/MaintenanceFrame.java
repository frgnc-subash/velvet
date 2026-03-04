package org.velvet.gui.scheduler;

import org.velvet.AppContext;
import org.velvet.gui.GuiUtil;
import org.velvet.model.hall.Hall;
import org.velvet.model.issue.MaintenanceTask;
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

public class MaintenanceFrame extends JFrame {
    private final User scheduler;
    private final JTextField filterField;
    private final DefaultTableModel model;
    private final JTable table;

    public MaintenanceFrame(User scheduler) {
        this.scheduler = scheduler;

        setTitle("Maintenance Schedule");
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

        model = new DefaultTableModel(new Object[]{"Task ID", "Hall ID", "Hall Name", "Start", "End", "Remarks", "Scheduler ID"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(model);
        root.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel actions = new JPanel();
        JButton addButton = new JButton("Add Task");
        JButton editButton = new JButton("Edit Task");
        JButton deleteButton = new JButton("Delete Task");
        JButton refreshButton = new JButton("Refresh");
        actions.add(addButton);
        actions.add(editButton);
        actions.add(deleteButton);
        actions.add(refreshButton);
        root.add(actions, BorderLayout.SOUTH);

        searchButton.addActionListener(e -> refreshTable());
        refreshButton.addActionListener(e -> {
            filterField.setText("");
            refreshTable();
        });

        addButton.addActionListener(e -> addTask());
        editButton.addActionListener(e -> editTask());
        deleteButton.addActionListener(e -> deleteTask());

        setContentPane(root);
        refreshTable();
    }

    private void refreshTable() {
        model.setRowCount(0);
        List<MaintenanceTask> tasks = AppContext.SCHEDULER_CONTROLLER.searchMaintenanceTasks(filterField.getText());
        for (MaintenanceTask task : tasks) {
            model.addRow(new Object[]{
                    task.getId(), task.getHallId(), task.getHallName(),
                    DateTimeUtil.formatDateTime(task.getStart()),
                    DateTimeUtil.formatDateTime(task.getEnd()),
                    task.getRemarks(), task.getSchedulerId()
            });
        }
    }

    private void addTask() {
        TaskForm form = showTaskForm(null);
        if (form == null) {
            return;
        }

        try {
            AppContext.SCHEDULER_CONTROLLER.addMaintenanceTask(form.hallId, form.start, form.end, form.remarks, scheduler.getId());
            refreshTable();
        } catch (Exception e) {
            GuiUtil.showError(this, e);
        }
    }

    private void editTask() {
        int row = table.getSelectedRow();
        if (row < 0) {
            GuiUtil.showInfo(this, "Select a task first.");
            return;
        }

        MaintenanceTask current = new MaintenanceTask();
        current.setId(model.getValueAt(row, 0).toString());
        current.setHallId(model.getValueAt(row, 1).toString());
        current.setHallName(model.getValueAt(row, 2).toString());
        current.setStart(DateTimeUtil.parseDateTime(model.getValueAt(row, 3).toString()));
        current.setEnd(DateTimeUtil.parseDateTime(model.getValueAt(row, 4).toString()));
        current.setRemarks(model.getValueAt(row, 5).toString());

        TaskForm form = showTaskForm(current);
        if (form == null) {
            return;
        }

        try {
            AppContext.SCHEDULER_CONTROLLER.updateMaintenanceTask(current.getId(), form.hallId, form.start, form.end, form.remarks);
            refreshTable();
        } catch (Exception e) {
            GuiUtil.showError(this, e);
        }
    }

    private void deleteTask() {
        int row = table.getSelectedRow();
        if (row < 0) {
            GuiUtil.showInfo(this, "Select a task first.");
            return;
        }

        String taskId = model.getValueAt(row, 0).toString();
        int option = JOptionPane.showConfirmDialog(this, "Delete maintenance task " + taskId + "?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (option != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            AppContext.SCHEDULER_CONTROLLER.deleteMaintenanceTask(taskId);
            refreshTable();
        } catch (Exception e) {
            GuiUtil.showError(this, e);
        }
    }

    private TaskForm showTaskForm(MaintenanceTask task) {
        List<Hall> halls = AppContext.HALL_SERVICE.getAllHalls();
        if (halls.isEmpty()) {
            GuiUtil.showInfo(this, "No halls found. Add a hall first.");
            return null;
        }

        JComboBox<String> hallBox = new JComboBox<>(halls.stream().map(h -> h.getId() + " - " + h.getName()).toArray(String[]::new));
        JTextField startField = new JTextField(task == null ? "" : DateTimeUtil.formatDateTime(task.getStart()));
        JTextField endField = new JTextField(task == null ? "" : DateTimeUtil.formatDateTime(task.getEnd()));
        JTextField remarksField = new JTextField(task == null ? "" : task.getRemarks());

        if (task != null) {
            for (int i = 0; i < hallBox.getItemCount(); i++) {
                String value = hallBox.getItemAt(i);
                if (value.startsWith(task.getHallId() + " ")) {
                    hallBox.setSelectedIndex(i);
                    break;
                }
            }
        }

        JPanel panel = new JPanel(new GridLayout(0, 2, 8, 8));
        panel.add(new JLabel("Hall:"));
        panel.add(hallBox);
        panel.add(new JLabel("Start (yyyy-MM-dd HH:mm):"));
        panel.add(startField);
        panel.add(new JLabel("End (yyyy-MM-dd HH:mm):"));
        panel.add(endField);
        panel.add(new JLabel("Remarks:"));
        panel.add(remarksField);

        int option = JOptionPane.showConfirmDialog(this, panel, task == null ? "Add Maintenance Task" : "Edit Maintenance Task",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (option != JOptionPane.OK_OPTION) {
            return null;
        }

        TaskForm form = new TaskForm();
        String selected = hallBox.getSelectedItem().toString();
        form.hallId = selected.split(" - ")[0].trim();
        form.start = DateTimeUtil.parseDateTime(startField.getText().trim());
        form.end = DateTimeUtil.parseDateTime(endField.getText().trim());
        form.remarks = remarksField.getText().trim();
        return form;
    }

    private static class TaskForm {
        private String hallId;
        private LocalDateTime start;
        private LocalDateTime end;
        private String remarks;
    }
}
