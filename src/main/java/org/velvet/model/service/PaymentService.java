package org.velvet.model.service;

import org.velvet.exception.PaymentFailedException;
import org.velvet.model.booking.Booking;
import org.velvet.model.booking.BookingStatus;
import org.velvet.model.payment.Payment;
import org.velvet.model.payment.Receipt;
import org.velvet.util.DateTimeUtil;
import org.velvet.util.FileHandler;
import org.velvet.util.IdGenerator;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PaymentService {
    private static final Path PAYMENTS_FILE = Path.of("src/main/resources/data/payments.txt");

    public PaymentService() {
        FileHandler.ensureFile(PAYMENTS_FILE);
    }

    public Payment processPayment(Booking booking, String method) throws PaymentFailedException {
        if (booking == null) {
            throw new PaymentFailedException("Booking is missing.");
        }
        if (method == null || method.trim().isEmpty()) {
            throw new PaymentFailedException("Payment method is required.");
        }
        if (booking.getTotalAmount() <= 0) {
            throw new PaymentFailedException("Invalid payment amount.");
        }
        if (booking.getStatus() == BookingStatus.CANCELLED || booking.getStatus() == BookingStatus.COMPLETED) {
            throw new PaymentFailedException("Payment is not allowed for this booking status.");
        }
        if (findByBookingId(booking.getId()) != null) {
            throw new PaymentFailedException("Payment already exists for this booking.");
        }

        Payment payment = new Payment(
                IdGenerator.generate("PAY"),
                booking.getId(),
                booking.getCustomerId(),
                booking.getTotalAmount(),
                method.trim(),
                "PAID",
                LocalDateTime.now()
        );

        List<Payment> payments = getAllPayments();
        payments.add(payment);
        saveAll(payments);
        return payment;
    }

    public Payment findByBookingId(String bookingId) {
        return getAllPayments().stream()
                .filter(payment -> payment.getBookingId().equals(bookingId))
                .findFirst()
                .orElse(null);
    }

    public List<Payment> getAllPayments() {
        List<String> lines = FileHandler.readAllLines(PAYMENTS_FILE);
        List<Payment> payments = new ArrayList<>();
        for (String line : lines) {
            if (line == null || line.isBlank()) {
                continue;
            }
            payments.add(parsePayment(line));
        }
        return payments;
    }

    public Receipt generateReceipt(Booking booking, Payment payment) {
        String text = "Receipt ID: " + payment.getId() + "\n"
                + "Booking ID: " + booking.getId() + "\n"
                + "Customer: " + booking.getCustomerName() + "\n"
                + "Hall: " + booking.getHallName() + " (" + booking.getHallType() + ")\n"
                + "Booking Time: " + DateTimeUtil.formatDateTime(booking.getStart()) + " to " + DateTimeUtil.formatDateTime(booking.getEnd()) + "\n"
                + "Amount: RM " + String.format("%.2f", payment.getAmount()) + "\n"
                + "Method: " + payment.getMethod() + "\n"
                + "Status: " + payment.getStatus() + "\n"
                + "Paid At: " + DateTimeUtil.formatDateTime(payment.getPaidAt());
        return new Receipt(text);
    }

    private Payment parsePayment(String line) {
        String[] parts = line.split("\\|", -1);
        if (parts.length < 7) {
            throw new IllegalArgumentException("Corrupted payment record: " + line);
        }
        return new Payment(
                parts[0],
                parts[1],
                parts[2],
                Double.parseDouble(parts[3]),
                parts[4],
                parts[5],
                DateTimeUtil.parseDateTime(parts[6])
        );
    }

    private void saveAll(List<Payment> payments) {
        List<String> lines = payments.stream().map(Payment::toRecord).collect(Collectors.toList());
        FileHandler.writeAllLines(PAYMENTS_FILE, lines);
    }
}
