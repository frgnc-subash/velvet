package org.velvet.model.service;

import org.velvet.exception.BookingNotAllowedException;
import org.velvet.model.booking.Booking;
import org.velvet.model.booking.BookingStatus;
import org.velvet.model.booking.TimeSlot;
import org.velvet.model.hall.Hall;
import org.velvet.model.user.Customer;
import org.velvet.util.DateTimeUtil;
import org.velvet.util.FileHandler;
import org.velvet.util.IdGenerator;
import org.velvet.util.ValidationUtil;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class BookingService {
    private static final Path BOOKINGS_FILE = Path.of("src/main/resources/data/bookings.txt");

    public BookingService() {
        FileHandler.ensureFile(BOOKINGS_FILE);
    }

    public Booking createBooking(Customer customer, String hallId, LocalDateTime start, LocalDateTime end, HallService hallService) {
        if (customer == null) {
            throw new IllegalArgumentException("Customer is required.");
        }
        ValidationUtil.validateDateRange(start, end);
        if (!DateTimeUtil.isWithinBusinessHours(start, end)) {
            throw new IllegalArgumentException("Booking must be within operating hours (08:00 - 18:00).");
        }
        if (!start.isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("Booking start time must be in the future.");
        }

        Hall hall = hallService.findHallById(hallId);
        if (hall == null) {
            throw new IllegalArgumentException("Hall not found.");
        }

        TimeSlot requested = new TimeSlot(start, end);
        if (!hall.isWithinAvailability(requested)) {
            throw new IllegalArgumentException("Requested time is outside the hall availability schedule.");
        }

        if (hallService.isHallUnderMaintenance(hallId, start, end)) {
            throw new IllegalArgumentException("Hall is under maintenance during the selected time.");
        }

        boolean clash = getAllBookings().stream()
                .filter(booking -> booking.getHallId().equals(hallId))
                .filter(booking -> booking.getStatus() == BookingStatus.PENDING || booking.getStatus() == BookingStatus.CONFIRMED)
                .anyMatch(booking -> booking.getTimeSlot().overlaps(requested));

        if (clash) {
            throw new IllegalArgumentException("Hall is already booked for the selected time.");
        }

        double hours = DateTimeUtil.minutesBetween(start, end) / 60.0;
        double total = roundTwoDecimals(hours * hall.getRatePerHour());

        Booking booking = new Booking(
                IdGenerator.generate("BKG"),
                customer.getId(),
                customer.getName(),
                hall.getId(),
                hall.getName(),
                hall.getType(),
                start,
                end,
                total,
                BookingStatus.PENDING,
                LocalDateTime.now()
        );

        List<Booking> bookings = getAllBookings();
        bookings.add(booking);
        saveAll(bookings);
        return booking;
    }

    public void cancelBooking(String bookingId, String customerId) throws BookingNotAllowedException {
        List<Booking> bookings = getAllBookings();
        Booking booking = bookings.stream().filter(b -> b.getId().equals(bookingId)).findFirst()
                .orElseThrow(() -> new BookingNotAllowedException("Booking not found."));

        if (!booking.getCustomerId().equals(customerId)) {
            throw new BookingNotAllowedException("You can only cancel your own booking.");
        }
        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new BookingNotAllowedException("Booking is already cancelled.");
        }
        if (booking.getStatus() == BookingStatus.COMPLETED) {
            throw new BookingNotAllowedException("Completed booking cannot be cancelled.");
        }
        if (!DateTimeUtil.atLeastDaysBefore(booking.getStart(), 3)) {
            throw new BookingNotAllowedException("Cancellation must be at least 3 days before booking date/time.");
        }

        booking.cancelBooking();
        saveAll(bookings);
    }

    public void confirmBooking(String bookingId) {
        List<Booking> bookings = getAllBookings();
        Booking booking = bookings.stream().filter(b -> b.getId().equals(bookingId)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Booking not found."));

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new IllegalArgumentException("Cancelled booking cannot be confirmed.");
        }
        booking.confirmBooking();
        saveAll(bookings);
    }

    public Booking findBookingById(String bookingId) {
        return getAllBookings().stream()
                .filter(booking -> booking.getId().equals(bookingId))
                .findFirst()
                .orElse(null);
    }

    public List<Booking> getAllBookings() {
        List<String> lines = FileHandler.readAllLines(BOOKINGS_FILE);
        List<Booking> bookings = new ArrayList<>();
        for (String line : lines) {
            if (line == null || line.isBlank()) {
                continue;
            }
            bookings.add(parseBooking(line));
        }
        bookings.sort(Comparator.comparing(Booking::getStart));
        return bookings;
    }

    public List<Booking> getBookingsByCustomer(String customerId) {
        return getAllBookings().stream()
                .filter(booking -> booking.getCustomerId().equals(customerId))
                .collect(Collectors.toList());
    }

    public List<Booking> getUpcomingBookingsByCustomer(String customerId) {
        LocalDateTime now = LocalDateTime.now();
        return getBookingsByCustomer(customerId).stream()
                .filter(booking -> booking.getStatus() == BookingStatus.PENDING || booking.getStatus() == BookingStatus.CONFIRMED)
                .filter(booking -> booking.getStart().isAfter(now))
                .collect(Collectors.toList());
    }

    public List<Booking> getPastBookingsByCustomer(String customerId) {
        LocalDateTime now = LocalDateTime.now();
        return getBookingsByCustomer(customerId).stream()
                .filter(booking -> booking.getEnd().isBefore(now)
                        || booking.getStatus() == BookingStatus.CANCELLED
                        || booking.getStatus() == BookingStatus.COMPLETED)
                .collect(Collectors.toList());
    }

    public List<Booking> filterBookingsForAdmin(String type, String keyword) {
        LocalDateTime now = LocalDateTime.now();
        String filter = type == null ? "ALL" : type.trim().toUpperCase(Locale.ROOT);
        String key = keyword == null ? "" : keyword.trim().toLowerCase(Locale.ROOT);

        return getAllBookings().stream()
                .filter(booking -> switch (filter) {
                    case "UPCOMING" -> (booking.getStatus() == BookingStatus.PENDING || booking.getStatus() == BookingStatus.CONFIRMED)
                            && booking.getStart().isAfter(now);
                    case "PAST" -> booking.getEnd().isBefore(now)
                            || booking.getStatus() == BookingStatus.CANCELLED
                            || booking.getStatus() == BookingStatus.COMPLETED;
                    default -> true;
                })
                .filter(booking -> key.isEmpty() ||
                        booking.getId().toLowerCase(Locale.ROOT).contains(key) ||
                        booking.getCustomerName().toLowerCase(Locale.ROOT).contains(key) ||
                        booking.getHallName().toLowerCase(Locale.ROOT).contains(key))
                .collect(Collectors.toList());
    }

    private Booking parseBooking(String line) {
        String[] parts = line.split("\\|", -1);
        if (parts.length < 11) {
            throw new IllegalArgumentException("Corrupted booking record: " + line);
        }

        return new Booking(
                parts[0],
                parts[1],
                parts[2],
                parts[3],
                parts[4],
                parts[5],
                DateTimeUtil.parseDateTime(parts[6]),
                DateTimeUtil.parseDateTime(parts[7]),
                Double.parseDouble(parts[8]),
                BookingStatus.valueOf(parts[9]),
                DateTimeUtil.parseDateTime(parts[10])
        );
    }

    private void saveAll(List<Booking> bookings) {
        List<String> lines = bookings.stream().map(Booking::toRecord).collect(Collectors.toList());
        FileHandler.writeAllLines(BOOKINGS_FILE, lines);
    }

    private double roundTwoDecimals(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
