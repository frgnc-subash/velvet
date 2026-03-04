package org.velvet.gui.customer;

import org.velvet.AppContext;
import org.velvet.exception.PaymentFailedException;
import org.velvet.gui.GuiUtil;
import org.velvet.model.booking.Booking;
import org.velvet.model.payment.Payment;
import org.velvet.model.payment.Receipt;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.GridLayout;

public class PaymentFrame extends JFrame {
    private final Booking booking;
    private final Runnable onSuccess;

    public PaymentFrame(Booking booking, Runnable onSuccess) {
        this.booking = booking;
        this.onSuccess = onSuccess;

        setTitle("Payment - Booking " + booking.getId());
        setSize(420, 240);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(new EmptyBorder(15, 15, 15, 15));

        JPanel info = new JPanel(new GridLayout(0, 1, 5, 5));
        info.add(new JLabel("Booking ID: " + booking.getId()));
        info.add(new JLabel("Hall: " + booking.getHallName() + " (" + booking.getHallType() + ")"));
        info.add(new JLabel(String.format("Amount: RM %.2f", booking.getTotalAmount())));
        root.add(info, BorderLayout.NORTH);

        JComboBox<String> methodBox = new JComboBox<>(new String[]{"Online Banking", "Credit Card", "Debit Card", "Cash"});
        JPanel center = new JPanel(new GridLayout(0, 1, 8, 8));
        center.add(new JLabel("Payment Method:"));
        center.add(methodBox);
        root.add(center, BorderLayout.CENTER);

        JPanel actions = new JPanel();
        JButton payButton = new JButton("Pay Now");
        JButton closeButton = new JButton("Close");
        actions.add(payButton);
        actions.add(closeButton);
        root.add(actions, BorderLayout.SOUTH);

        payButton.addActionListener(e -> {
            try {
                Payment payment = AppContext.CUSTOMER_CONTROLLER.processPayment(booking, methodBox.getSelectedItem().toString());
                Receipt receipt = AppContext.CUSTOMER_CONTROLLER.generateReceipt(booking, payment);
                new ReceiptFrame(receipt).setVisible(true);
                if (onSuccess != null) {
                    onSuccess.run();
                }
                dispose();
            } catch (PaymentFailedException ex) {
                GuiUtil.showError(this, ex);
            }
        });

        closeButton.addActionListener(e -> dispose());

        setContentPane(root);
    }
}
