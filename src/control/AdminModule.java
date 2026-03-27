package control;

import adt.BookingDQ;
import adt.LinkedDeque;
import adt.UserDQ;
import adt.VenueDQ;
import dao.*;
import model.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.Scanner;

/**
 * Admin module — all administrative operations.
 */
public class AdminModule {

    private final Scanner sc;

    public AdminModule(Scanner sc) {
        this.sc = sc;
    }

    /** Main admin menu loop. */
    public void run() {
        int choice;
        do {
            System.out.println("\n========= ADMIN PANEL =========");
            System.out.println(" 1. Create Venue");
            System.out.println(" 2. Update Venue");
            System.out.println(" 3. Remove Venue");
            System.out.println(" 4. Search Venue");
            System.out.println(" 5. Block / Unblock Venue");
            System.out.println(" 6. Accept Registrations");
            System.out.println(" 7. Waitlist Management");
            System.out.println(" 8. Slots View");
            System.out.println(" 9. Reports");
            System.out.println("10. Registered User Lists");
            System.out.println("11. History Actions");
            System.out.println(" 0. Logout");
            System.out.println("================================");
            System.out.print("Choice: ");
            choice = readInt();

            switch (choice) {
                case 1 -> createVenue();
                case 2 -> updateVenue();
                case 3 -> removeVenue();
                case 4 -> searchVenue();
                case 5 -> blockVenue();
                case 6 -> acceptRegistrations();
                case 7 -> waitlistManagement();
                case 8 -> slotsView();
                case 9 -> reports();
                case 10 -> registeredUserLists();
                case 11 -> historyActions();
                case 0 -> System.out.println("Logging out...");
                default -> System.out.println("Invalid choice.");
            }
        } while (choice != 0);
    }

    // ------------------------------------------------------------------ Venue CRUD

    private void createVenue() {
        VenueDQ<Venue> venues = VenueDatabase.loadVenues();
        String id = ValidationUtils.readNonBlankString(sc, "Venue ID: ");
        if (venues.find(id) != null) {
            System.out.println("Venue ID already exists!");
            return;
        }
        String name = ValidationUtils.readNonBlankString(sc, "Venue Name: ");
        System.out.print("Capacity: ");
        int cap = readInt();
        Venue v = new Venue(id, name, cap);
        venues.addLast(v);
        VenueDatabase.saveVenues(venues);
        logHistory("VENUE_CREATED", "ADMIN", id, "Created venue: " + name);
        System.out.println("Venue created successfully.");
    }

    private void updateVenue() {
        VenueDQ<Venue> venues = VenueDatabase.loadVenues();
        if (venues.isEmpty()) {
            System.out.println("No venues available to update.");
            return;
        }

        System.out.println("\n--- Available Venues ---");
        for (Venue v : venues) {
            System.out.println("  [" + v.getVenueId() + "] " + v.getVenueName() + " (Cap: " + v.getCapacity() + ")");
        }

        String id = ValidationUtils.readNonBlankString(sc, "\nEnter Venue ID to update (or 'back'): ");
        if ("back".equalsIgnoreCase(id))
            return;

        Venue v = (Venue) venues.find(id);
        if (v == null) {
            System.out.println("Venue not found.");
            return;
        }
        System.out.println("Current: " + v);
        System.out.print("New Name (blank to keep): ");
        String name = sc.nextLine().trim(); // Allowing blank here deliberately
        if (!name.isEmpty())
            v.setVenueName(name);
        System.out.print("New Capacity (0 to keep): ");
        int cap = readInt();
        if (cap > 0)
            v.setCapacity(cap);
        VenueDatabase.saveVenues(venues);
        logHistory("VENUE_UPDATED", "ADMIN", id, "Updated venue");
        System.out.println("Venue updated.");
    }

    private void removeVenue() {
        VenueDQ<Venue> venues = VenueDatabase.loadVenues();
        if (venues.isEmpty()) {
            System.out.println("No venues available to remove.");
            return;
        }

        System.out.println("\n--- Available Venues ---");
        for (Venue v : venues) {
            System.out.println("  [" + v.getVenueId() + "] " + v.getVenueName());
        }

        String id = ValidationUtils.readNonBlankString(sc, "\nEnter Venue ID to remove (or 'back'): ");
        if ("back".equalsIgnoreCase(id))
            return;

        VenueDQ<Venue> updated = new VenueDQ<>();
        boolean found = false;
        for (Venue v : venues) {
            if (v.getVenueId().equals(id)) {
                found = true;
            } else {
                updated.addLast(v);
            }
        }
        if (!found) {
            System.out.println("Venue not found.");
            return;
        }
        VenueDatabase.saveVenues(updated);
        logHistory("VENUE_REMOVED", "ADMIN", id, "Removed venue");
        System.out.println("Venue removed.");
    }

    private void searchVenue() {
        VenueDQ<Venue> venues = VenueDatabase.loadVenues();
        System.out.print("Search by (1) ID  or  (2) Name keyword: ");
        int opt = readInt();
        if (opt == 1) {
            String id = ValidationUtils.readNonBlankString(sc, "Venue ID: ");
            Venue v = (Venue) venues.find(id);
            System.out.println(v == null ? "Not found." : v);
        } else {
            String kw = ValidationUtils.readNonBlankString(sc, "Keyword: ").toLowerCase();
            boolean any = false;
            for (Venue v : venues) {
                if (v.getVenueName().toLowerCase().contains(kw)) {
                    System.out.println(v);
                    any = true;
                }
            }
            if (!any)
                System.out.println("No venues matched.");
        }
    }

    private void blockVenue() {
        VenueDQ<Venue> venues = VenueDatabase.loadVenues();
        if (venues.isEmpty()) {
            System.out.println("No venues available to block/unblock.");
            return;
        }

        System.out.println("\n--- Available Venues ---");
        for (Venue v : venues) {
            System.out.println("  [" + v.getVenueId() + "] " + v.getVenueName() + " (Status: " + v.getStatus() + ")");
        }

        String id = ValidationUtils.readNonBlankString(sc, "\nEnter Venue ID to block/unblock (or 'back'): ");
        if ("back".equalsIgnoreCase(id))
            return;

        Venue v = (Venue) venues.find(id);
        if (v == null) {
            System.out.println("Venue not found.");
            return;
        }

        System.out.print("Venue: " + v.getVenueName() + "\n");
        System.out.println("Current status: " + v.getStatus());
        System.out.print("Set to (1) BLOCKED  (2) MAINTENANCE  (3) AVAILABLE: ");
        int opt = readInt();
        switch (opt) {
            case 1 -> v.setStatus(VenueStatus.BLOCKED);
            case 2 -> v.setStatus(VenueStatus.MAINTENANCE);
            case 3 -> v.setStatus(VenueStatus.AVAILABLE);
            default -> {
                System.out.println("Invalid.");
                return;
            }
        }
        VenueDatabase.saveVenues(venues);
        logHistory("VENUE_STATUS", "ADMIN", id, "Status -> " + v.getStatus());
        System.out.println("Venue status updated to " + v.getStatus());
    }

    // ------------------------------------------------------------------
    // Registrations

    private void acceptRegistrations() {
        LinkedDeque<Registration> regs = RegistrationDatabase.loadRegistrations();
        if (regs.isEmpty()) {
            System.out.println("No pending registrations.");
            return;
        }

        // Show pending ones
        int idx = 1;
        LinkedDeque<Registration> pendingList = new LinkedDeque<>();
        for (Registration r : regs) {
            if ("PENDING".equals(r.getStatus())) {
                System.out.println(idx + ". " + r);
                pendingList.addLast(r);
                idx++;
            }
        }
        if (pendingList.isEmpty()) {
            System.out.println("No pending registrations.");
            return;
        }

        String regId = ValidationUtils.readNonBlankString(sc, "Enter Registration ID to review (or 'back'): ");
        if ("back".equalsIgnoreCase(regId))
            return;

        // Find it
        Registration target = null;
        for (Registration r : regs) {
            if (r.getId().equals(regId) && "PENDING".equals(r.getStatus())) {
                target = r;
                break;
            }
        }
        if (target == null) {
            System.out.println("Registration not found or not pending.");
            return;
        }

        System.out.println("Details: " + target);
        System.out.print("(1) Approve  (2) Reject: ");
        int opt = readInt();
        if (opt == 1) {
            target.setStatus("APPROVED");
            // Create actual user
            UserDQ<User> users = UserDatabase.loadUsers();
            UserRole role = UserRole.valueOf(target.getRole());
            String priv = target.getFacilityPrivilege();
            User newUser = new User(target.getId(), "default123", target.getName(),
                    role, UserStatus.APPROVED, priv);
            users.addLast(newUser);
            UserDatabase.saveUsers(users);
            System.out.println("Registration approved. User created with default password 'default123'.");
            logHistory("REG_APPROVED", "ADMIN", "-", "Approved user: " + target.getId());
        } else {
            target.setStatus("REJECTED");
            System.out.println("Registration rejected.");
            logHistory("REG_REJECTED", "ADMIN", "-", "Rejected user: " + target.getId());
        }
        RegistrationDatabase.saveRegistrations(regs);
    }

    // ------------------------------------------------------------------ Waitlist

    private void waitlistManagement() {
        BookingDQ<Booking> bookings = BookingDatabase.loadBookings();
        System.out.println("\n--- Waitlisted Bookings ---");
        boolean any = false;
        for (Booking b : bookings) {
            if (b.getBookingStatus() == BookingStatus.WAITING) {
                System.out.println(b);
                any = true;
            }
        }
        if (!any) {
            System.out.println("No waitlisted bookings.");
            return;
        }

        String bid = ValidationUtils.readNonBlankString(sc, "\nRemove from waitlist? Enter Booking ID (or 'back'): ");
        if ("back".equalsIgnoreCase(bid))
            return;

        Booking target = (Booking) bookings.find(bid);
        if (target == null || target.getBookingStatus() != BookingStatus.WAITING) {
            System.out.println("Booking not found on waitlist.");
            return;
        }
        target.setBookingStatus(BookingStatus.CANCELLED);
        BookingDatabase.saveBookings(bookings);
        logHistory("WAITLIST_REMOVED", "ADMIN", target.getVenueId(),
                "Removed " + target.getUserId() + " from waitlist");
        System.out.println("Booking removed from waitlist.");
    }

    // ------------------------------------------------------------------ Slots View

    private void slotsView() {
        VenueDQ<Venue> venues = VenueDatabase.loadVenues();
        BookingDQ<Booking> bookings = BookingDatabase.loadBookings();

        System.out.println("\n============ SLOTS VIEW ============");
        for (Venue v : venues) {
            System.out.println("\n[" + v.getVenueId() + "] " + v.getVenueName()
                    + "  (Status: " + v.getStatus() + ", Cap: " + v.getCapacity() + ")");
            System.out.printf("  %-12s %-10s %-10s %-15s %-10s%n",
                    "BookingID", "Date", "Time", "User", "Status");
            System.out.println("  " + "-".repeat(60));
            boolean hasBooking = false;
            for (Booking b : bookings) {
                if (b.getVenueId().equals(v.getVenueId())) {
                    System.out.printf("  %-12s %-10s %-10s %-15s %-10s%n",
                            b.getBookingId(), b.getDate(),
                            b.getStartTime() + "-" + b.getEndTime(),
                            b.getUserId(), b.getBookingStatus());
                    hasBooking = true;
                }
            }
            if (!hasBooking)
                System.out.println("  (no bookings)");
        }
    }

    // ------------------------------------------------------------------ Reports

    private void reports() {
        BookingDQ<Booking> bookings = BookingDatabase.loadBookings();
        VenueDQ<Venue> venues = VenueDatabase.loadVenues();
        UserDQ<User> users = UserDatabase.loadUsers();

        System.out.println("\n========== REPORTS ==========");
        System.out.println("1. Venue Utilization Summary");
        System.out.println("2. Booking Status Breakdown");
        System.out.println("3. Most Active Users");
        System.out.println("4. Peak Time Analysis");
        System.out.println("0. Back");
        System.out.print("Choice: ");
        int opt = readInt();

        switch (opt) {
            case 1 -> reportVenueUtilization(venues, bookings);
            case 2 -> reportBookingStatus(bookings);
            case 3 -> reportActiveUsers(bookings, users);
            case 4 -> reportPeakTime(bookings);
        }
    }

    private void reportVenueUtilization(VenueDQ<Venue> venues, BookingDQ<Booking> bookings) {
        System.out.println("\n--- Venue Utilization ---");
        System.out.printf("%-10s %-25s %-8s %-10s %-10s%n",
                "ID", "Name", "Status", "Bookings", "Waitlist");
        System.out.println("-".repeat(65));
        for (Venue v : venues) {
            int active = 0, waiting = 0;
            for (Booking b : bookings) {
                if (b.getVenueId().equals(v.getVenueId())) {
                    if (b.getBookingStatus() == BookingStatus.ACTIVE)
                        active++;
                    else if (b.getBookingStatus() == BookingStatus.WAITING)
                        waiting++;
                }
            }
            System.out.printf("%-10s %-25s %-8s %-10d %-10d%n",
                    v.getVenueId(), v.getVenueName(), v.getStatus(), active, waiting);
        }
    }

    private void reportBookingStatus(BookingDQ<Booking> bookings) {
        int active = 0, waiting = 0, cancelled = 0, completed = 0, forfeited = 0;
        for (Booking b : bookings) {
            switch (b.getBookingStatus()) {
                case ACTIVE -> active++;
                case WAITING -> waiting++;
                case CANCELLED -> cancelled++;
                case COMPLETED -> completed++;
                case FORFEITED -> forfeited++;
            }
        }
        System.out.println("\n--- Booking Status Breakdown ---");
        System.out.println("Active    : " + active);
        System.out.println("Waiting   : " + waiting);
        System.out.println("Cancelled : " + cancelled);
        System.out.println("Completed : " + completed);
        System.out.println("Forfeited : " + forfeited);
        System.out.println("Total     : " + bookings.size());
    }

    private void reportActiveUsers(BookingDQ<Booking> bookings, UserDQ<User> users) {
        System.out.println("\n--- Most Active Users (by booking count) ---");
        // Build a simple count using the deque
        LinkedDeque<String> countedIds = new LinkedDeque<>();
        LinkedDeque<Integer> counts = new LinkedDeque<>();

        for (Booking b : bookings) {
            String uid = b.getUserId();
            boolean found = false;
            // Walk both deques in parallel using iterators
            Iterator<String> idIt = countedIds.iterator();
            Iterator<Integer> cntIt = counts.iterator();
            LinkedDeque<String> newIds = new LinkedDeque<>();
            LinkedDeque<Integer> newCnts = new LinkedDeque<>();

            while (idIt.hasNext()) {
                String existId = idIt.next();
                int existCnt = cntIt.next();
                if (existId.equals(uid)) {
                    existCnt++;
                    found = true;
                }
                newIds.addLast(existId);
                newCnts.addLast(existCnt);
            }
            if (!found) {
                newIds.addLast(uid);
                newCnts.addLast(1);
            }
            countedIds = newIds;
            counts = newCnts;
        }

        // Print
        System.out.printf("%-15s %-20s %-10s%n", "User ID", "Name", "Bookings");
        System.out.println("-".repeat(45));
        Iterator<String> idIt = countedIds.iterator();
        Iterator<Integer> cntIt = counts.iterator();
        while (idIt.hasNext()) {
            String uid = idIt.next();
            int cnt = cntIt.next();
            User u = (User) users.find(uid);
            String uname = (u != null) ? u.getName() : "Unknown";
            System.out.printf("%-15s %-20s %-10d%n", uid, uname, cnt);
        }
    }

    private void reportPeakTime(BookingDQ<Booking> bookings) {
        System.out.println("\n--- Peak Time Analysis (by start hour) ---");
        // Count bookings per start-time hour
        LinkedDeque<String> hours = new LinkedDeque<>();
        LinkedDeque<Integer> counts = new LinkedDeque<>();

        for (Booking b : bookings) {
            String hour = b.getStartTime(); // e.g. "09:00"
            boolean found = false;
            Iterator<String> hIt = hours.iterator();
            Iterator<Integer> cIt = counts.iterator();
            LinkedDeque<String> newH = new LinkedDeque<>();
            LinkedDeque<Integer> newC = new LinkedDeque<>();

            while (hIt.hasNext()) {
                String eh = hIt.next();
                int ec = cIt.next();
                if (eh.equals(hour)) {
                    ec++;
                    found = true;
                }
                newH.addLast(eh);
                newC.addLast(ec);
            }
            if (!found) {
                newH.addLast(hour);
                newC.addLast(1);
            }
            hours = newH;
            counts = newC;
        }

        System.out.printf("%-10s %-10s%n", "Time", "Bookings");
        System.out.println("-".repeat(20));
        Iterator<String> hIt = hours.iterator();
        Iterator<Integer> cIt = counts.iterator();
        while (hIt.hasNext()) {
            System.out.printf("%-10s %-10d%n", hIt.next(), cIt.next());
        }
    }

    // ------------------------------------------------------------------ User Lists

    private void registeredUserLists() {
        UserDQ<User> users = UserDatabase.loadUsers();
        System.out.println("\n--- Registered Users ---");
        System.out.printf("%-15s %-20s %-15s %-10s %-15s%n",
                "ID", "Name", "Role", "Status", "Privilege");
        System.out.println("-".repeat(75));
        for (User u : users) {
            System.out.printf("%-15s %-20s %-15s %-10s %-15s%n",
                    u.getStudentId(), u.getName(), u.getRole(),
                    u.getStatus(), u.getFacilityPrivilege());
        }
    }

    // ------------------------------------------------------------------ History

    private void historyActions() {
        LinkedDeque<History> history = HistoryDatabase.loadHistory();
        if (history.isEmpty()) {
            System.out.println("No history records.");
            return;
        }
        System.out.println("\n--- Action History ---");
        System.out.printf("%-20s %-18s %-15s %-10s %s%n",
                "Timestamp", "Action", "User", "Venue", "Details");
        System.out.println("-".repeat(85));
        for (History h : history) {
            System.out.printf("%-20s %-18s %-15s %-10s %s%n",
                    h.getTimestamp(), h.getAction(), h.getUserId(),
                    h.getVenueId(), h.getDetails());
        }
    }

    // ------------------------------------------------------------------ Helpers

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
