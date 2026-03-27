package control;

import adt.BookingDQ;
import adt.LinkedDeque;
import adt.UserDQ;
import adt.VenueDQ;
import dao.*;
import model.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

/**
 * Student module — booking, waitlist, slot viewing.
 */
public class StudentModule {

    private final Scanner sc;
    private User currentUser;

    public StudentModule(Scanner sc) {
        this.sc = sc;
    }

    /** Login then enter the student menu. Returns false if login fails. */
    public boolean loginAndRun() {
        currentUser = login();
        if (currentUser == null) return false;
        run();
        return true;
    }

    private User login() {
        String id = ValidationUtils.readStudentId(sc, "Student ID: ");
        String pw = ValidationUtils.readNonBlankString(sc, "Password: ");

        UserDQ<User> users = UserDatabase.loadUsers();
        User u = (User) users.find(id);
        if (u == null) {
            System.out.println("User not found. Please register first.");
            return null;
        }
        if (u.getStatus() != UserStatus.APPROVED) {
            System.out.println("Account not approved yet. Status: " + u.getStatus());
            return null;
        }
        if (u.getRole() != UserRole.NORMAL_USER) {
            System.out.println("This login is for normal students only.");
            return null;
        }
        if (!u.getPassword().equals(pw)) {
            System.out.println("Wrong password.");
            return null;
        }
        System.out.println("Welcome, " + u.getName() + "!");
        return u;
    }

    private void run() {
        int choice;
        do {
            System.out.println("\n===== STUDENT MENU =====");
            System.out.println("1. Book Facility");
            System.out.println("2. Waitlist Registration");
            System.out.println("3. Slots View");
            System.out.println("4. My Bookings");
            System.out.println("0. Logout");
            System.out.println("========================");
            System.out.print("Choice: ");
            choice = readInt();

            switch (choice) {
                case 1 -> bookFacility();
                case 2 -> waitlistRegistration();
                case 3 -> slotsView();
                case 4 -> myBookings();
                case 0 -> System.out.println("Logging out...");
                default -> System.out.println("Invalid choice.");
            }
        } while (choice != 0);
    }

    private void bookFacility() {
        VenueDQ<Venue> venues = VenueDatabase.loadVenues();
        BookingDQ<Booking> bookings = BookingDatabase.loadBookings();

        // Show available venues
        System.out.println("\n--- Available Venues ---");
        for (Venue v : venues) {
            if (v.isAvailable()) {
                System.out.println("  " + v.getVenueId() + " - " + v.getVenueName()
                        + " (Cap: " + v.getCapacity() + ")");
            }
        }

        String vid = ValidationUtils.readNonBlankString(sc, "Venue ID: ");
        Venue venue = (Venue) venues.find(vid);
        if (venue == null || !venue.isAvailable()) {
            System.out.println("Venue not available.");
            return;
        }

        String date = ValidationUtils.readDate(sc, "Date (yyyy-MM-dd): ");
        String start = ValidationUtils.readTime(sc, "Start Time (HH:mm): ");
        String end = ValidationUtils.readTime(sc, "End Time (HH:mm): ");

        // Check for conflicts
        for (Booking b : bookings) {
            if (b.isSameSlot(vid, date, start, end)
                    && b.getBookingStatus() == BookingStatus.ACTIVE) {
                System.out.println("Slot already booked. Use 'Waitlist Registration' to join the waitlist.");
                return;
            }
        }

        String bid = "BK" + String.format("%04d", bookings.size() + 1);
        Booking booking = new Booking(bid, currentUser.getStudentId(), vid,
                date, start, end, BookingStatus.ACTIVE);
        bookings.addLast(booking);
        BookingDatabase.saveBookings(bookings);
        logHistory("BOOKED", currentUser.getStudentId(), vid,
                "Booked " + date + " " + start + "-" + end);
        System.out.println("Booking confirmed! ID: " + bid);
    }

    private void waitlistRegistration() {
        VenueDQ<Venue> venues = VenueDatabase.loadVenues();
        BookingDQ<Booking> bookings = BookingDatabase.loadBookings();

        String vid = ValidationUtils.readNonBlankString(sc, "Venue ID: ");
        Venue venue = (Venue) venues.find(vid);
        if (venue == null) {
            System.out.println("Venue not found.");
            return;
        }

        String date = ValidationUtils.readDate(sc, "Date (yyyy-MM-dd): ");
        String start = ValidationUtils.readTime(sc, "Start Time (HH:mm): ");
        String end = ValidationUtils.readTime(sc, "End Time (HH:mm): ");

        String bid = "BK" + String.format("%04d", bookings.size() + 1);
        Booking booking = new Booking(bid, currentUser.getStudentId(), vid,
                date, start, end, BookingStatus.WAITING);
        bookings.addLast(booking);
        BookingDatabase.saveBookings(bookings);
        logHistory("WAITLISTED", currentUser.getStudentId(), vid,
                "Joined waitlist " + date + " " + start + "-" + end);
        System.out.println("Added to waitlist. ID: " + bid);
    }

    private void slotsView() {
        VenueDQ<Venue> venues = VenueDatabase.loadVenues();
        BookingDQ<Booking> bookings = BookingDatabase.loadBookings();

        System.out.println("\n============ SLOTS VIEW ============");
        for (Venue v : venues) {
            System.out.println("\n[" + v.getVenueId() + "] " + v.getVenueName()
                    + "  (Status: " + v.getStatus() + ")");
            System.out.printf("  %-12s %-12s %-12s %-10s%n", "Date", "Time", "Status", "BookingID");
            System.out.println("  " + "-".repeat(50));
            boolean hasBooking = false;
            for (Booking b : bookings) {
                if (b.getVenueId().equals(v.getVenueId())
                        && (b.getBookingStatus() == BookingStatus.ACTIVE
                        || b.getBookingStatus() == BookingStatus.WAITING)) {
                    System.out.printf("  %-12s %-12s %-12s %-10s%n",
                            b.getDate(), b.getStartTime() + "-" + b.getEndTime(),
                            b.getBookingStatus(), b.getBookingId());
                    hasBooking = true;
                }
            }
            if (!hasBooking) System.out.println("  (all slots open)");
        }
    }

    private void myBookings() {
        BookingDQ<Booking> bookings = BookingDatabase.loadBookings();
        System.out.println("\n--- My Bookings ---");
        boolean any = false;
        for (Booking b : bookings) {
            if (b.getUserId().equals(currentUser.getStudentId())) {
                System.out.println("  " + b);
                any = true;
            }
        }
        if (!any) System.out.println("  No bookings found.");
    }

    // ---- Helpers ----

    private void logHistory(String action, String userId, String venueId, String details) {
        LinkedDeque<History> history = HistoryDatabase.loadHistory();
        String ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        history.addLast(new History(ts, action, userId, venueId, details));
        HistoryDatabase.saveHistory(history);
    }

    private int readInt() {
        return ValidationUtils.readInt(sc, "");
    }
}
