package org.velvet.gui;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

public final class GuiUtil {
    private GuiUtil() {
    }

    public static void showError(JFrame parent, Exception e) {
        JOptionPane.showMessageDialog(parent, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void showInfo(JFrame parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    public static String ask(JFrame parent, String message, String title) {
        return JOptionPane.showInputDialog(parent, message, title, JOptionPane.PLAIN_MESSAGE);
    }
}
