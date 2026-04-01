package control;

import utils.ValidationUtils;
import utils.BookingUtils;
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
 * @author HENG TIAN LI
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
        String id = ValidationUtils.readNonBlankString(sc, "Privileged User ID: ");
        String pw = ValidationUtils.readNonBlankString(sc, "Password: ");

        UserDQ users = UserDatabase.loadUsers();
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
            System.out.println("4. Cancel Booking");
            System.out.println("5. Edit My Details");
            System.out.println("0. Logout");
            System.out.println("===========================");
            System.out.print("Choice: ");
            choice = readInt();

            switch (choice) {
                case 1 -> expressBooking();
                case 2 -> slotsView();
                case 3 -> myBookings();
                case 4 -> cancelBooking();
                case 5 -> editMyDetails();
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

        String vid = ValidationUtils.readNonBlankString(sc, "Enter a Venue ID (press Q to exit): ");
        if (vid.equalsIgnoreCase("q")) {
            System.out.println("Exiting...");
            return;
        }
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
        String[] times = selectPrivilegedTimeRange();
        String start = times[0];
        String end = times[1];

        int newStartH = Integer.parseInt(start.split(":")[0]);
        int newEndH = Integer.parseInt(end.split(":")[0]);

        // Check conflicts — if slot is taken, displace that booking to WAITING
        for (Booking b : bookings) {
            if (b.getVenueId().equals(vid) && b.getDate().equals(date) && b.getBookingStatus() == BookingStatus.ACTIVE) {
                int bStartH = Integer.parseInt(b.getStartTime().split(":")[0]);
                int bEndH = Integer.parseInt(b.getEndTime().split(":")[0]);
                
                // Overlap condition
                if (Math.max(newStartH, bStartH) < Math.min(newEndH, bEndH)) {
                    b.setBookingStatus(BookingStatus.WAITING);
                    System.out.println("Displaced booking " + b.getBookingId()
                            + " (" + b.getUserId() + ") to waitlist.");
                    logHistory("EXPRESS_DISPLACED", currentUser.getStudentId(), vid,
                            "Displaced " + b.getBookingId() + " to waitlist");
                }
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
        if ("NONE".equalsIgnoreCase(privilege)) return false;
        return venue.getVenueId().equalsIgnoreCase(privilege);
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
        System.out.println(String.format("| %-9s | %-10s | %-7s | %-10s | %-11s | %-9s |",
                "BookingID", "UserID", "VenueID", "Date", "Time", "Status"));
        for (Booking b : bookings) {
            if (b.getUserId().equals(currentUser.getStudentId())) {
                System.out.println(b);
                any = true;
            }
        }
        if (!any) System.out.println("  No bookings found.");
    }

    private void cancelBooking() {
        BookingDQ<Booking> bookings = BookingDatabase.loadBookings();
        System.out.println("\n--- Cancel My Booking ---");
        
        boolean found = false;
        for (Booking b : bookings) {
            if (b.getUserId().equals(currentUser.getStudentId()) && b.getBookingStatus() == BookingStatus.ACTIVE) {
                System.out.println("  [" + b.getBookingId() + "] Venue: " + b.getVenueId() + 
                                   " | " + b.getDate() + " (" + b.getStartTime() + "-" + b.getEndTime() + ")");
                found = true;
            }
        }
        
        if (!found) {
            System.out.println("No active bookings available to cancel.");
            return;
        }

        String bid = ValidationUtils.readNonBlankString(sc, "\nEnter Booking ID to cancel (or 'back'): ");
        if ("back".equalsIgnoreCase(bid)) return;

        Booking target = (Booking) bookings.find(bid);
        if (target == null || !target.getUserId().equals(currentUser.getStudentId()) || target.getBookingStatus() != BookingStatus.ACTIVE) {
            System.out.println("Invalid Booking ID or booking is not active.");
            return;
        }
        
        System.out.print("Are you sure you want to cancel " + bid + "? (Y/N): ");
        String confirm = sc.nextLine().trim();
        if (confirm.equalsIgnoreCase("y")) {
            target.setBookingStatus(BookingStatus.CANCELLED);
            BookingDatabase.saveBookings(bookings);
            logHistory("BOOKING_CANCELLED", currentUser.getStudentId(), target.getVenueId(), "Cancelled booking " + bid);
            System.out.println("Booking cancelled successfully.");
            
            // Trigger Waitlist Promotion
            BookingUtils.promoteWaitlist(target);
        } else {
            System.out.println("Cancellation aborted.");
        }
    }

    private void editMyDetails() {
        System.out.println("\n--- Edit My Details ---");
        String pw;
        while (true) {
            System.out.print("Enter new password (leave blank to skip): ");
            pw = sc.nextLine().trim();
            if (pw.isEmpty()) {
                System.out.println("No changes made.");
                return;
            }
            if (pw.contains(",")) {
                System.out.println("Error: Password cannot contain commas.");
                continue;
            }
            if (pw.length() < 5) {
                System.out.println("Error: Password must be at least 5 characters long.");
                continue;
            }
            break;
        }

        currentUser.setPassword(pw);
        UserDQ users = UserDatabase.loadUsers();
        User uInDb = (User) users.find(currentUser.getStudentId());
        if (uInDb != null) {
            uInDb.setPassword(pw);
            UserDatabase.saveUsers(users);
            System.out.println("User details updated successfully.");
            logHistory("USER_UPDATED", currentUser.getStudentId(), "-", "Changed password");
        } else {
            System.out.println("Error: User not found in database.");
        }
    }

    // ---- Helpers ----

    private String[] selectPrivilegedTimeRange() {
        System.out.println("\nChoose Start Time:");
        int[] availableStarts = new int[12];
        int startCount = 0;
        for (int i = 8; i <= 19; i++) {
            System.out.printf("%d. %02d:00\n", startCount + 1, i);
            availableStarts[startCount++] = i;
        }

        int startChoice;
        while (true) {
            startChoice = ValidationUtils.readInt(sc, "Enter choice: ");
            if (startChoice >= 1 && startChoice <= startCount) {
                break;
            }
            System.out.println("Invalid choice.");
        }
        int selectedStart = availableStarts[startChoice - 1];

        System.out.println("\nChoose End Time:");
        int[] availableEnds = new int[12];
        int endCount = 0;

        for (int end = selectedStart + 1; end <= 20; end++) {
            System.out.printf("%d. %02d:00\n", endCount + 1, end);
            availableEnds[endCount++] = end;
        }

        int endChoice;
        while (true) {
            endChoice = ValidationUtils.readInt(sc, "Enter choice: ");
            if (endChoice >= 1 && endChoice <= endCount) {
                break;
            }
            System.out.println("Invalid choice.");
        }
        int selectedEnd = availableEnds[endChoice - 1];

        return new String[]{
            String.format("%02d:00", selectedStart), 
            String.format("%02d:00", selectedEnd)
        };
    }

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
