package org.velvet.gui.customer;

import org.velvet.model.payment.Receipt;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;

public class ReceiptFrame extends JFrame {
    public ReceiptFrame(Receipt receipt) {
        setTitle("Payment Receipt");
        setSize(500, 360);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JTextArea area = new JTextArea(receipt.getReceiptText());
        area.setEditable(false);
        area.setBorder(new EmptyBorder(10, 10, 10, 10));

        setLayout(new BorderLayout());
        add(new JScrollPane(area), BorderLayout.CENTER);
    }
}
