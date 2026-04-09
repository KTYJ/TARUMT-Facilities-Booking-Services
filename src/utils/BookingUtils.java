/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package utils;

/**
 *
 * @author HENG TIAN LI
 */
import adt.BookingDQ;
import dao.BookingDatabase;
import model.Booking;
import model.status.BookingStatus;

public class BookingUtils {

    /**
     * Promotes the eligible WAITLIST bookings to ACTIVE after an active booking is
     * cancelled or forfeited.
     * Uses greedy left-to-right filling and resolves tiebreakers based on queue
     * priority natively handled by BookingDQ.
     */
    public static void promoteWaitlist(Booking cancelledBooking) {
        java.time.LocalTime cancelledStart = java.time.LocalTime.parse(cancelledBooking.getStartTime().toString());
        java.time.LocalTime cancelledEnd = java.time.LocalTime.parse(cancelledBooking.getEndTime().toString());
        long maxPromotions = java.time.Duration.between(cancelledStart, cancelledEnd).toHours();

        BookingDQ<Booking> allBookings;
        try {
            allBookings = BookingDatabase.loadBookings();
        } catch (Exception e) {
            System.out.println("Error loading bookings for waitlist promotion: " + e.getMessage());
            return;
        }

        // Step 1 - Collect candidates
        BookingDQ<Booking> candidates = new BookingDQ<>();
        for (Booking b : allBookings) {
            if (b.getBookingStatus() == BookingStatus.WAITING &&
                    b.getVenueId().equals(cancelledBooking.getVenueId()) &&
                    b.getDate().equals(cancelledBooking.getDate())) {

                java.time.LocalTime bStart = java.time.LocalTime.parse(b.getStartTime().toString());
                java.time.LocalTime bEnd = java.time.LocalTime.parse(b.getEndTime().toString());

                if (!bStart.isBefore(cancelledStart) && !bEnd.isAfter(cancelledEnd)) {
                    candidates.addLast(b);
                }
            }
        }

        // Step 2 - Fill the cancelled slot greedily, left to right
        java.time.LocalTime coveragePointer = cancelledStart;
        int promotions = 0;

        while (!coveragePointer.equals(cancelledEnd) && !candidates.isEmpty()) {
            if (promotions >= maxPromotions)
                break;

            // Peek at the front — earliest enqueued booking has highest priority
            Booking front = (Booking) candidates.peekFirst();
            java.time.LocalTime frontStart = java.time.LocalTime.parse(front.getStartTime().toString());

            if (!frontStart.equals(coveragePointer)) {
                break; // Front of the deque does not start at the coverage pointer — gap, stop
            }

            // Remove and select the front booking
            Booking selected = (Booking) candidates.removeFirst();

            selected.setBookingStatus(BookingStatus.ACTIVE);
            promotions++;
            System.out.println("[WAITLIST] Booking " + selected.getBookingId() + " promoted to ACTIVE for venue "
                    + selected.getVenueId() + " on " + selected.getDate() + " (" + selected.getStartTime() + "–"
                    + selected.getEndTime() + ").");

            coveragePointer = java.time.LocalTime.parse(selected.getEndTime().toString());
        }

        // Console Output rules
        if (coveragePointer.equals(cancelledEnd)) {
            System.out.println("[WAITLIST] Cancelled slot fully filled for venue " + cancelledBooking.getVenueId() +
                    " on " + cancelledBooking.getDate() + " (" + cancelledBooking.getStartTime() + "–"
                    + cancelledBooking.getEndTime() + ").");
        }

        if (promotions == 0) {
            System.out.println("[WAITLIST] No waiting bookings to promote for venue " + cancelledBooking.getVenueId() +
                    " on " + cancelledBooking.getDate() + ".");
        } else {
            try {
                BookingDatabase.saveBookings(allBookings);
            } catch (Exception e) {
                System.out.println("Error saving waitlist promotions: " + e.getMessage());
            }
        }
    }
}
