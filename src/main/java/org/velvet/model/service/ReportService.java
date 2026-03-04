package org.velvet.model.service;

import org.velvet.model.booking.Booking;
import org.velvet.model.booking.BookingStatus;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.stream.Collectors;

public class ReportService {
    private final BookingService bookingService;

    public ReportService(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    public double getWeeklySales() {
        LocalDate today = LocalDate.now();
        LocalDate startDate = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate endDate = startDate.plusDays(6);
        return getSalesBetween(startDate, endDate);
    }

    public double getMonthlySales() {
        YearMonth current = YearMonth.now();
        return getSalesBetween(current.atDay(1), current.atEndOfMonth());
    }

    public double getYearlySales() {
        int year = LocalDate.now().getYear();
        return getSalesBetween(LocalDate.of(year, 1, 1), LocalDate.of(year, 12, 31));
    }

    public double getSalesBetween(LocalDate fromDate, LocalDate toDate) {
        return getBookingsBetween(fromDate, toDate).stream()
                .mapToDouble(Booking::getTotalAmount)
                .sum();
    }

    public List<Booking> getBookingsBetween(LocalDate fromDate, LocalDate toDate) {
        LocalDateTime from = fromDate.atStartOfDay();
        LocalDateTime to = toDate.atTime(LocalTime.MAX);
        return bookingService.getAllBookings().stream()
                .filter(booking -> booking.getStatus() == BookingStatus.CONFIRMED || booking.getStatus() == BookingStatus.COMPLETED)
                .filter(booking -> !booking.getStart().isBefore(from) && !booking.getStart().isAfter(to))
                .collect(Collectors.toList());
    }
}
