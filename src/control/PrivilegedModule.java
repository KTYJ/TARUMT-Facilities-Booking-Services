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
 * Privileged-user module — express booking on assigned venues.
 */
public class PrivilegedModule {

    private final Scanner sc;
    private User currentUser;

    public PrivilegedModule(Scanner sc) {
        this.sc = sc;
    }

    /** Login then enter the privileged menu. Returns false if login fails. */
    public boolean loginAndRun() {
        currentUser = login();
        if (currentUser == null) return false;
        run();
        return true;
    }

    private User login() {
        String id = ValidationUtils.readUserId(sc, "Privileged User ID: ");
        String pw = ValidationUtils.readNonBlankString(sc, "Password: ");

        UserDQ<User> users = UserDatabase.loadUsers();
        User u = (User) users.find(id);
        if (u == null) {
            System.out.println("User not found.");
            return null;
        }
        if (u.getStatus() != UserStatus.APPROVED) {
            System.out.println("Account not approved. Status: " + u.getStatus());
            return null;
        }
        if (u.getRole() != UserRole.PRIVILEGED_USER) {
            System.out.println("This login is for privileged users only.");
            return null;
        }
        if (!u.getPassword().equals(pw)) {
            System.out.println("Wrong password.");
            return null;
        }
        System.out.println("Welcome, " + u.getName() + "! (Privilege: " + u.getFacilityPrivilege() + ")");
        return u;
    }

    private void run() {
        int choice;
        do {
            System.out.println("\n===== PRIVILEGED MENU =====");
            System.out.println("1. Express Booking");
            System.out.println("2. Slots View");
            System.out.println("3. My Bookings");
            System.out.println("0. Logout");
            System.out.println("===========================");
            System.out.print("Choice: ");
            choice = readInt();

            switch (choice) {
                case 1 -> expressBooking();
                case 2 -> slotsView();
                case 3 -> myBookings();
                case 0 -> System.out.println("Logging out...");
                default -> System.out.println("Invalid choice.");
            }
        } while (choice != 0);
    }

    /**
     * Express booking — can override an existing ACTIVE booking on
     * assigned venues. The displaced booking moves to WAITING status.
     */
    private void expressBooking() {
        VenueDQ<Venue> venues = VenueDatabase.loadVenues();
        BookingDQ<Booking> bookings = BookingDatabase.loadBookings();
        String privilege = currentUser.getFacilityPrivilege();

        // Show eligible venues
        System.out.println("\n--- Eligible Venues ---");
        for (Venue v : venues) {
            if (isPrivilegeMatch(v, privilege) && v.isAvailable()) {
                System.out.println("  " + v.getVenueId() + " - " + v.getVenueName());
            }
        }

        String vid = ValidationUtils.readNonBlankString(sc, "Venue ID: ");
        Venue venue = (Venue) venues.find(vid);
        if (venue == null || !venue.isAvailable()) {
            System.out.println("Venue not available.");
            return;
        }
        if (!isPrivilegeMatch(venue, privilege)) {
            System.out.println("You do not have privilege for this venue.");
            return;
        }

        String date = ValidationUtils.readDate(sc, "Date (yyyy-MM-dd): ");
        String start = ValidationUtils.readTime(sc, "Start Time (HH:mm): ");
        String end = ValidationUtils.readTime(sc, "End Time (HH:mm): ");

        // Check conflicts — if slot is taken, displace that booking to WAITING
        for (Booking b : bookings) {
            if (b.isSameSlot(vid, date, start, end)
                    && b.getBookingStatus() == BookingStatus.ACTIVE) {
                b.setBookingStatus(BookingStatus.WAITING);
                System.out.println("Displaced booking " + b.getBookingId()
                        + " (" + b.getUserId() + ") to waitlist.");
                logHistory("EXPRESS_DISPLACED", currentUser.getStudentId(), vid,
                        "Displaced " + b.getBookingId() + " to waitlist");
            }
        }

        String bid = "BK" + String.format("%04d", bookings.size() + 1);
        Booking booking = new Booking(bid, currentUser.getStudentId(), vid,
                date, start, end, BookingStatus.ACTIVE);
        bookings.addLast(booking);
        BookingDatabase.saveBookings(bookings);
        logHistory("EXPRESS_BOOKED", currentUser.getStudentId(), vid,
                "Express booked " + date + " " + start + "-" + end);
        System.out.println("Express booking confirmed! ID: " + bid);
    }

    /** Check if the user's privilege covers this venue. */
    private boolean isPrivilegeMatch(Venue venue, String privilege) {
        if ("ALL".equalsIgnoreCase(privilege)) return true;
        // Match on venue name containing the privilege keyword
        return venue.getVenueName().toUpperCase().contains(privilege.toUpperCase());
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
