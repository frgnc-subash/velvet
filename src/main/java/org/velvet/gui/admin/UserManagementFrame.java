package org.velvet.gui.admin;

import org.velvet.AppContext;
import org.velvet.gui.GuiUtil;
import org.velvet.model.user.User;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.List;

public class UserManagementFrame extends JFrame {
    private final JTextField schedulerFilter;
    private final DefaultTableModel schedulerModel;
    private final JTable schedulerTable;

    private final JTextField userFilter;
    private final DefaultTableModel userModel;
    private final JTable userTable;

    public UserManagementFrame() {
        setTitle("Scheduler & User Management");
        setSize(1040, 640);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JTabbedPane tabbedPane = new JTabbedPane();

        JPanel schedulerPanel = new JPanel(new BorderLayout(10, 10));
        schedulerPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel schedulerTop = new JPanel(new BorderLayout(8, 8));
        schedulerFilter = new JTextField();
        JButton schedulerSearchButton = new JButton("Search");
        schedulerTop.add(new JLabel("Filter:"), BorderLayout.WEST);
        schedulerTop.add(schedulerFilter, BorderLayout.CENTER);
        schedulerTop.add(schedulerSearchButton, BorderLayout.EAST);
        schedulerPanel.add(schedulerTop, BorderLayout.NORTH);

        schedulerModel = new DefaultTableModel(new Object[]{"ID", "Name", "Username", "Phone", "Email", "Blocked"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        schedulerTable = new JTable(schedulerModel);
        schedulerPanel.add(new JScrollPane(schedulerTable), BorderLayout.CENTER);

        JPanel schedulerActions = new JPanel();
        JButton addScheduler = new JButton("Add Scheduler");
        JButton editScheduler = new JButton("Edit Scheduler");
        JButton deleteScheduler = new JButton("Delete Scheduler");
        JButton refreshScheduler = new JButton("Refresh");
        schedulerActions.add(addScheduler);
        schedulerActions.add(editScheduler);
        schedulerActions.add(deleteScheduler);
        schedulerActions.add(refreshScheduler);
        schedulerPanel.add(schedulerActions, BorderLayout.SOUTH);

        JPanel userPanel = new JPanel(new BorderLayout(10, 10));
        userPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel userTop = new JPanel(new BorderLayout(8, 8));
        userFilter = new JTextField();
        JButton userSearchButton = new JButton("Search");
        userTop.add(new JLabel("Filter:"), BorderLayout.WEST);
        userTop.add(userFilter, BorderLayout.CENTER);
        userTop.add(userSearchButton, BorderLayout.EAST);
        userPanel.add(userTop, BorderLayout.NORTH);

        userModel = new DefaultTableModel(new Object[]{"ID", "Role", "Name", "Username", "Phone", "Email", "Blocked"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        userTable = new JTable(userModel);
        userPanel.add(new JScrollPane(userTable), BorderLayout.CENTER);

        JPanel userActions = new JPanel();
        JButton blockUser = new JButton("Block User");
        JButton unblockUser = new JButton("Unblock User");
        JButton deleteUser = new JButton("Delete User");
        JButton refreshUser = new JButton("Refresh");
        userActions.add(blockUser);
        userActions.add(unblockUser);
        userActions.add(deleteUser);
        userActions.add(refreshUser);
        userPanel.add(userActions, BorderLayout.SOUTH);

        tabbedPane.addTab("Scheduler Staff", schedulerPanel);
        tabbedPane.addTab("Users", userPanel);

        setContentPane(tabbedPane);

        schedulerSearchButton.addActionListener(e -> loadSchedulers());
        refreshScheduler.addActionListener(e -> {
            schedulerFilter.setText("");
            loadSchedulers();
        });
        addScheduler.addActionListener(e -> addScheduler());
        editScheduler.addActionListener(e -> editScheduler());
        deleteScheduler.addActionListener(e -> deleteScheduler());

        userSearchButton.addActionListener(e -> loadUsers());
        refreshUser.addActionListener(e -> {
            userFilter.setText("");
            loadUsers();
        });
        blockUser.addActionListener(e -> blockSelectedUser(true));
        unblockUser.addActionListener(e -> blockSelectedUser(false));
        deleteUser.addActionListener(e -> deleteSelectedUser());

        loadSchedulers();
        loadUsers();
    }

    private void loadSchedulers() {
        schedulerModel.setRowCount(0);
        List<User> schedulers = AppContext.ADMIN_CONTROLLER.getSchedulers(schedulerFilter.getText());
        for (User user : schedulers) {
            schedulerModel.addRow(new Object[]{
                    user.getId(), user.getName(), user.getUsername(), user.getPhone(), user.getEmail(), user.isBlocked()
            });
        }
    }

    private void loadUsers() {
        userModel.setRowCount(0);
        List<User> users = AppContext.ADMIN_CONTROLLER.searchUsers(userFilter.getText());
        for (User user : users) {
            userModel.addRow(new Object[]{
                    user.getId(), user.getRole(), user.getName(), user.getUsername(), user.getPhone(), user.getEmail(), user.isBlocked()
            });
        }
    }

    private void addScheduler() {
        UserForm form = showUserForm(null, "Add Scheduler");
        if (form == null) {
            return;
        }
        try {
            AppContext.ADMIN_CONTROLLER.addScheduler(form.name, form.username, form.password, form.phone, form.email);
            loadSchedulers();
            loadUsers();
        } catch (Exception e) {
            GuiUtil.showError(this, e);
        }
    }

    private void editScheduler() {
        int row = schedulerTable.getSelectedRow();
        if (row < 0) {
            GuiUtil.showInfo(this, "Select a scheduler first.");
            return;
        }

        String userId = schedulerModel.getValueAt(row, 0).toString();
        User existing = AppContext.USER_SERVICE.findById(userId);
        UserForm form = showUserForm(existing, "Edit Scheduler");
        if (form == null) {
            return;
        }

        try {
            AppContext.ADMIN_CONTROLLER.updateScheduler(userId, form.name, form.username, form.password, form.phone, form.email, form.blocked);
            loadSchedulers();
            loadUsers();
        } catch (Exception e) {
            GuiUtil.showError(this, e);
        }
    }

    private void deleteScheduler() {
        int row = schedulerTable.getSelectedRow();
        if (row < 0) {
            GuiUtil.showInfo(this, "Select a scheduler first.");
            return;
        }

        String userId = schedulerModel.getValueAt(row, 0).toString();
        int option = JOptionPane.showConfirmDialog(this, "Delete scheduler " + userId + "?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (option != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            AppContext.ADMIN_CONTROLLER.deleteScheduler(userId);
            loadSchedulers();
            loadUsers();
        } catch (Exception e) {
            GuiUtil.showError(this, e);
        }
    }

    private void blockSelectedUser(boolean blocked) {
        int row = userTable.getSelectedRow();
        if (row < 0) {
            GuiUtil.showInfo(this, "Select a user first.");
            return;
        }

        String userId = userModel.getValueAt(row, 0).toString();
        try {
            if (blocked) {
                AppContext.ADMIN_CONTROLLER.blockUser(userId);
            } else {
                AppContext.ADMIN_CONTROLLER.unblockUser(userId);
            }
            loadSchedulers();
            loadUsers();
        } catch (Exception e) {
            GuiUtil.showError(this, e);
        }
    }

    private void deleteSelectedUser() {
        int row = userTable.getSelectedRow();
        if (row < 0) {
            GuiUtil.showInfo(this, "Select a user first.");
            return;
        }

        String userId = userModel.getValueAt(row, 0).toString();
        int option = JOptionPane.showConfirmDialog(this, "Delete user " + userId + "?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (option != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            AppContext.ADMIN_CONTROLLER.deleteUser(userId);
            loadSchedulers();
            loadUsers();
        } catch (Exception e) {
            GuiUtil.showError(this, e);
        }
    }

    private UserForm showUserForm(User user, String title) {
        JTextField name = new JTextField(user == null ? "" : user.getName());
        JTextField username = new JTextField(user == null ? "" : user.getUsername());
        JTextField password = new JTextField(user == null ? "" : user.getPassword());
        JTextField phone = new JTextField(user == null ? "" : user.getPhone());
        JTextField email = new JTextField(user == null ? "" : user.getEmail());
        JTextField blocked = new JTextField(user == null ? "false" : String.valueOf(user.isBlocked()));

        JPanel panel = new JPanel(new GridLayout(0, 2, 8, 8));
        panel.add(new JLabel("Name:"));
        panel.add(name);
        panel.add(new JLabel("Username:"));
        panel.add(username);
        panel.add(new JLabel("Password:"));
        panel.add(password);
        panel.add(new JLabel("Phone:"));
        panel.add(phone);
        panel.add(new JLabel("Email:"));
        panel.add(email);
        panel.add(new JLabel("Blocked (true/false):"));
        panel.add(blocked);

        int option = JOptionPane.showConfirmDialog(this, panel, title, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (option != JOptionPane.OK_OPTION) {
            return null;
        }

        UserForm form = new UserForm();
        form.name = name.getText().trim();
        form.username = username.getText().trim();
        form.password = password.getText().trim();
        form.phone = phone.getText().trim();
        form.email = email.getText().trim();
        form.blocked = Boolean.parseBoolean(blocked.getText().trim());
        return form;
    }

    private static class UserForm {
        private String name;
        private String username;
        private String password;
        private String phone;
        private String email;
        private boolean blocked;
    }
}
