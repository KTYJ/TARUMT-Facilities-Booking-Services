/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package control;

import model.status.UserRole;
import model.status.BookingStatus;
import model.Booking;
import model.status.UserStatus;
import model.status.VenueStatus;
import utils.ValidationUtils;
import utils.BookingUtils;
import adt.BookingDQ;
import adt.LinkedDeque;
import adt.SorterDQ;
import adt.UserDQ;
import adt.VenueDQ;
import dao.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import model.*;

/**
 * Admin module — all administrative operations.
 * 
 * @author TAN JIN YUAN KTYJ
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
            System.out.println(" 1. Venue Management");
            System.out.println(" 2. Accept Registrations");
            System.out.println(" 3. Waitlist Management");
            System.out.println(" 4. Slots View");
            System.out.println(" 5. Update Bookings");
            System.out.println(" 6. Reports");
            System.out.println(" 7. Registered User Lists");
            System.out.println(" 8. Edit User Details");
            System.out.println(" 0. Logout");
            System.out.println("================================");
            System.out.print("Choice: ");
            choice = readInt();

            switch (choice) {
                case 1 -> venueManagement();
                case 2 -> acceptRegistrations();
                case 3 -> waitlistManagement();
                case 4 -> slotsView();
                case 5 -> manageBookings();
                case 6 -> reports();
                case 7 -> registeredUserLists();
                case 8 -> editUserDetails();
                case 0 -> System.out.println("Logging out...");
                default -> System.out.println("Invalid choice.");
            }
        } while (choice != 0);
    }

    private void venueManagement() {
        int choice;
        do {
            System.out.println("\n===== VENUE MANAGEMENT =====");
            System.out.println(" 1. Create Venue");
            System.out.println(" 2. Update / Block Venue");
            System.out.println(" 3. Remove Venue");
            System.out.println(" 4. Search Venue");
            System.out.println(" 5. View All Venues");
            System.out.println(" 0. Back to Admin Panel");
            System.out.println("-------------------------");
            System.out.print("Choice: ");
            choice = readInt();

            switch (choice) {
                case 1 -> createVenue();
                case 2 -> updateVenue();
                case 3 -> removeVenue();
                case 4 -> searchVenue();
                case 5 -> viewVenues();
                case 0 -> System.out.println("Returning to admin panel...");
                default -> System.out.println("Invalid choice.");
            }
        } while (choice != 0);
    }

    // ------------------------------------------------------------------ Venue CRUD

    private void createVenue() {
        VenueDQ<Venue> venues = VenueDatabase.loadVenues();
        String id;

        while (true) {
            id = ValidationUtils.readNonBlankString(sc,
                    "Venue ID ([Letter]+4 digits, e.g., V0001) <press Q to exit>: ");
            if (id.equalsIgnoreCase("Q")) {
                return;
            } else if (Venue.isValidVenueId(id)) {
                break;
            } else {
                System.out.println("Wrong venue format, please try with [Letter]+4 digits");
            }
        }

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

        boolean changed = false;

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
        if (!name.isEmpty()) {
            v.setVenueName(name);
            changed = true;
        }
        System.out.print("New Capacity (0 to keep): ");
        int cap = readInt();
        if (cap > 0) {
            v.setCapacity(cap);
            changed = true;
        }

        System.out.println("Current status: " + v.getStatus());
        System.out.print("New Status (1) BLOCKED  (2) MAINTENANCE  (3) AVAILABLE (0 to keep): ");
        int opt = readInt();
        if (opt != 0) {
            switch (opt) {
                case 1 -> {
                    v.setStatus(VenueStatus.BLOCKED);
                    changed = true;
                }
                case 2 -> {
                    v.setStatus(VenueStatus.MAINTENANCE);
                    changed = true;
                }
                case 3 -> {
                    v.setStatus(VenueStatus.AVAILABLE);
                    changed = true;
                }
                default -> System.out.println("Invalid default status choice, keeping current status.");
            }
            if (opt >= 1 && opt <= 3) {
                logHistory("VENUE_STATUS", "ADMIN", id, "Status -> " + v.getStatus());
            }
        }

        if (changed) {
            VenueDatabase.saveVenues(venues);
            logHistory("VENUE_UPDATED", "ADMIN", id, "Updated venue");
            System.out.println("Venue updated successfully.");
        } else {
            System.out.println("No changes made.");
        }
    }

    private void removeVenue() {
        VenueDQ<Venue> venues = VenueDatabase.loadVenues();
        BookingDQ<Booking> bookings = BookingDatabase.loadBookings();

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

        for (Booking b : bookings) {
            if (b.getVenueId().equals(id) && b.getBookingStatus() == BookingStatus.ACTIVE) {
                System.out.println("Error: Cannot remove this venue because it has active bookings.");
                return;
            }
        }

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

    private void viewVenues() {
        VenueDQ<Venue> venues = VenueDatabase.loadVenues();
        if (venues.isEmpty()) {
            System.out.println("\nNo venues registered yet.");
            return;
        }
        int opt;
        String crit = "Capacity"; //default
        do {
            venues.sortBy(crit);
            if (crit != null) {
                System.out.println("\n>> Successfully sorted by " + crit + ".");
            }
            System.out.println("\n--- All Venues ---");
            System.out.printf("%-15s %-30s %-10s %-15s%n", "ID", "Name", "Capacity", "Status");
            System.out.println("-".repeat(75));

            for (Venue v : venues) {
                System.out.printf("%-15s %-30s %-10d %-15s%n",
                        v.getVenueId(), v.getVenueName(), v.getCapacity(), v.getStatus());
            }
            System.out.print("Sort by...\n1. Name\n2. Capacity\n3. Status\n0. Back\nChoice: ");
            opt = readInt();

            if (opt == 1) {
                crit = "Name";
            } else if (opt == 2) {
                crit = "Capacity";
            } else if (opt == 3) {
                crit = "Status";
            }
            
        } while (opt != 0);
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
            UserDQ users = UserDatabase.loadUsers();
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
            BookingDQ<Booking> tempBDQ = new BookingDQ<>();
            for (Booking b : bookings) {
                if (b.getVenueId().equals(v.getVenueId())
                        && (b.getBookingStatus() == BookingStatus.ACTIVE
                                || b.getBookingStatus() == BookingStatus.WAITING)) {
                    tempBDQ.addLast(b);
                    hasBooking = true;
                }
            }
            if (hasBooking) {
                tempBDQ.sortByDateTime(false);
                for (Booking b : tempBDQ) {
                    System.out.println("  " + b);
                }
            } else
                System.out.println("  (no bookings)");
        }
    }

    // ------------------------------------------------------------------ Manage
    // Bookings

    private void manageBookings() {
        BookingDQ<Booking> bookings = BookingDatabase.loadBookings();
        if (bookings.isEmpty()) {
            System.out.println("No bookings available.");
            return;
        }

        System.out.println("\n--- All Bookings ---");
        bookings.sortByDateTime(false);
        for (Booking b : bookings) {
            System.out.println("  [" + b.getBookingId() + "] - " + b.getUserId() +
                    " - " + b.getVenueId() +
                    " (" + b.getDate() + " " + b.getStartTime() + "-" + b.getEndTime() + ") Status: "
                    + b.getBookingStatus());
        }

        String bid = ValidationUtils.readNonBlankString(sc, "\nEnter Booking ID to manage (or 'back'): ");
        if ("back".equalsIgnoreCase(bid))
            return;

        Booking target = (Booking) bookings.find(bid);
        if (target == null) {
            System.out.println("Booking not found.");
            return;
        }

        System.out.println("\nEditing Booking: " + target.getBookingId());
        System.out.println("Current Status: " + target.getBookingStatus());
        System.out.println("Statuses:");
        BookingStatus[] statuses = BookingStatus.values();
        for (int i = 0; i < statuses.length; i++) {
            System.out.println("  " + (i + 1) + ") " + statuses[i]);
        }

        while (true) {
            System.out.print("Select new status (enter number, leave blank to skip): ");
            String sChoice = sc.nextLine().trim();
            if (sChoice.isEmpty()) {
                System.out.println("No changes made.");
                return;
            }
            try {
                int sIdx = Integer.parseInt(sChoice);
                if (sIdx >= 1 && sIdx <= statuses.length) {
                    BookingStatus oldStatus = target.getBookingStatus();
                    BookingStatus newStatus = statuses[sIdx - 1];

                    target.setBookingStatus(newStatus);
                    BookingDatabase.saveBookings(bookings);
                    logHistory("BOOKING_STATUS", "ADMIN", target.getVenueId(),
                            "Updated booking " + target.getBookingId() + " to " + newStatus);
                    System.out.println("Booking status updated successfully.");

                    if (oldStatus == BookingStatus.ACTIVE && newStatus == BookingStatus.CANCELLED ||
                            oldStatus == BookingStatus.ACTIVE && newStatus == BookingStatus.FORFEITED) {
                        BookingUtils.promoteWaitlist(target);
                    }

                    break;
                } else {
                    System.out.println("Error: Invalid status number.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Error: Please enter a valid number.");
            }
        }
    }

    // ------------------------------------------------------------------ Reports

    private void reports() {
        int opt = 0;
        do {
            BookingDQ<Booking> bookings = BookingDatabase.loadBookings();
            VenueDQ<Venue> venues = VenueDatabase.loadVenues();
            UserDQ users = UserDatabase.loadUsers();

            System.out.println("\n========== REPORTS ==========");
            System.out.println("1. Venue Utilization Summary");
            System.out.println("2. Booking Status Breakdown");
            System.out.println("3. Most Active Users");
            System.out.println("4. Peak Time Analysis");
            System.out.println("5. History Actions");
            System.out.println("0. Back");
            System.out.print("Choice: ");
            opt = readInt();
            if (opt < 0 || opt > 5) {
                System.out.println("Invalid choice. Please try again.");
                continue;
            }
            switch (opt) {
                case 1 -> reportVenueUtilization(venues, bookings);
                case 2 -> reportBookingStatus(bookings);
                case 3 -> reportActiveUsers(bookings, users);
                case 4 -> reportPeakTime(bookings);
                case 5 -> historyActions();
            }
        } while (opt != 0);
    }

    private void reportVenueUtilization(VenueDQ<Venue> venues, BookingDQ<Booking> bookings) {
        System.out.println("\n--- Venue Utilization ---");
        System.out.printf("%-10s %-25s %-11s %-10s %-10s%n",
                "ID", "Name", "Status", "Bookings", "Waitlist");
        System.out.println("-".repeat(70));
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
            System.out.printf("%-10s %-25s %-11s %-11d %-11d%n",
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

    private void reportActiveUsers(BookingDQ<Booking> bookings, UserDQ users) {
        SorterDQ sorterDQ = new SorterDQ(); // rows: [userId, name, count]

        for (Booking b : bookings) {
            String uid = b.getUserId();
            boolean found = false;
            // if found, increment count
            for (String[] row : sorterDQ) {
                if (row[0].equals(uid)) {
                    row[2] = String.valueOf(Integer.parseInt(row[2]) + 1);
                    found = true;
                    break;
                }
            }
            // if not found, add new row of user
            if (!found) {
                User u = users.find(uid);
                String uname = (u != null) ? u.getName() : "Unknown";
                sorterDQ.addLast(new String[] { uid, uname, "1" });
            }
        }

        // ── 2. Sort descending by booking count (last element) ────────────────────
        sorterDQ.sort(true); // true = descending (most active first)

        // ── 3. Print ──────────────────────────────────────────────────────────────
        System.out.println("\n--- Most Active Users (by booking count) ---");
        System.out.printf("%-15s %-20s %-10s%n", "User ID", "Name", "Bookings");
        System.out.println("-".repeat(45));
        for (String[] row : sorterDQ) {
            System.out.printf("%-15s %-20s %-10s%n", row[0], row[1], row[2]);
        }
    }

    private void reportPeakTime(BookingDQ<Booking> bookings) {
        // ── 1. Tally bookings per start-time slot ─────────────────────────────────
        SorterDQ sorterDQ = new SorterDQ(); // rows: [hour, count]

        for (Booking b : bookings) {
            String hour = b.getStartTime(); // e.g. "09:00"
            boolean found = false;

            for (String[] row : sorterDQ) {
                if (row[0].equals(hour)) {
                    row[1] = String.valueOf(Integer.parseInt(row[1]) + 1);
                    found = true;
                    break;
                }
            }
            if (!found) {
                sorterDQ.addLast(new String[] { hour, "1" });
            }
        }

        // ── 2. Sort descending by booking count (last element) ────────────────────
        sorterDQ.sort(true); // true = descending (busiest slot first)

        // ── 3. Print ──────────────────────────────────────────────────────────────
        System.out.println("\n--- Peak Time Analysis (by start hour) ---");
        System.out.printf("%-10s %-10s%n", "Time", "Bookings");
        System.out.println("-".repeat(20));
        for (String[] row : sorterDQ) {
            System.out.printf("%-10s %-10s%n", row[0], row[1]);
        }
    }

    // ------------------------------------------------------------------ User Lists

    private void registeredUserLists() {
        UserDQ users = UserDatabase.loadUsers();
        int choice;

        do {
            System.out.println("\n--- Registered Users ---");
            System.out.printf("%-15s %-20s %-15s %-10s %-15s%n",
                    "ID", "Name", "Role", "Status", "Privilege");
            System.out.println("-".repeat(75));
            for (User u : users) {
                System.out.printf("%-15s %-20s %-15s %-10s %-15s%n",
                        u.getStudentId(), u.getName(), u.getRole(),
                        u.getStatus(), u.getFacilityPrivilege());
            }

            System.out.println("\nSort by...");
            System.out.println("1. Student ID");
            System.out.println("2. Name");
            System.out.println("3. Role");
            System.out.println("4. Status");
            System.out.println("0. Back");
            System.out.print("Choice: ");
            choice = readInt();

            String criterion = null;
            switch (choice) {
                case 1 -> criterion = "ID";
                case 2 -> criterion = "NAME";
                case 3 -> criterion = "ROLE";
                case 4 -> criterion = "STATUS";
                case 0 -> System.out.println("Returning to main menu...");
                default -> System.out.println("Invalid choice, please try again.");
            }

            if (criterion != null) {
                users.sortBy(criterion);
                System.out.println("\n>> Successfully sorted by " + criterion + ".");
            }
        } while (choice != 0);
    }

    // ------------------------------------------------------------------ Edit User
    // Details
    private void editUserDetails() {
        UserDQ users = UserDatabase.loadUsers();
        if (users.isEmpty()) {
            System.out.println("No users available.");
            return;
        }

        System.out.println("\n--- All Users ---");
        int count = 1;
        for (User u : users) {
            System.out.println("  " + count + ") [" + u.getStudentId() + "] " + u.getName() + " (" + u.getRole() + ")");
            count++;
        }

        System.out.print("\nEnter user number to edit (or 0 to go back): ");
        int choice = readInt();
        if (choice <= 0 || choice >= count) {
            return;
        }

        User target = users.get(choice - 1);

        System.out.println("\nEditing User: " + target);
        boolean changed = false;

        // 1. ID
        String newId;
        while (true) {
            System.out.print("Enter new ID (leave blank to skip): ");
            newId = sc.nextLine().trim();
            if (newId.isEmpty())
                break;

            if (users.find(newId) != null && !newId.equals(target.getStudentId())) {
                System.out.println("Error: ID already exists. Must be unique.");
                continue;
            }
            target.setStudentId(newId);
            changed = true;
            break;
        }

        // 2. Password
        String pw;
        while (true) {
            System.out.print("Enter new password (leave blank to skip): ");
            pw = sc.nextLine().trim();
            if (pw.isEmpty())
                break;
            if (pw.contains(",")) {
                System.out.println("Error: Password cannot contain commas.");
                continue;
            }
            if (pw.length() < 5) {
                System.out.println("Error: Password must be at least 5 characters long.");
                continue;
            }
            target.setPassword(pw);
            changed = true;
            break;
        }

        // 3. Name
        String newName;
        while (true) {
            System.out.print("Enter new name (leave blank to skip): ");
            newName = sc.nextLine().trim();
            if (newName.isEmpty())
                break;
            if (newName.contains(",")) {
                System.out.println("Error: Name cannot contain commas.");
                continue;
            }
            target.setName(newName);
            changed = true;
            break;
        }

        // 4. Role
        System.out.println("Roles:");
        UserRole[] roles = UserRole.values();
        for (int i = 0; i < roles.length; i++) {
            System.out.println("  " + (i + 1) + ") " + roles[i]);
        }
        while (true) {
            System.out.print("Select new role (enter number, leave blank to skip): ");
            String rChoice = sc.nextLine().trim();
            if (rChoice.isEmpty())
                break;
            try {
                int rIdx = Integer.parseInt(rChoice);
                if (rIdx >= 1 && rIdx <= roles.length) {
                    target.setRole(roles[rIdx - 1]);
                    changed = true;
                    break;
                } else {
                    System.out.println("Error: Invalid role number.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Error: Please enter a valid number.");
            }
        }

        // 5. Status
        System.out.println("Statuses:");
        System.out.println("  1) APPROVED");
        System.out.println("  2) SUSPENDED");
        while (true) {
            System.out.print("Select new status (enter number, leave blank to skip): ");
            String sChoice = sc.nextLine().trim();
            if (sChoice.isEmpty())
                break;
            if (sChoice.equals("1")) {
                target.setStatus(UserStatus.APPROVED);
                changed = true;
                break;
            } else if (sChoice.equals("2")) {
                target.setStatus(UserStatus.SUSPENDED);
                changed = true;
                break;
            } else {
                System.out.println("Error: Invalid status number. Must be 1 or 2.");
            }
        }

        // 6. Facility Privilege
        if (target.getRole() == UserRole.PRIVILEGED_USER) {
            VenueDQ<Venue> venues = VenueDatabase.loadVenues();
            System.out.println("Facility Privileges:");
            System.out.println("  1) NONE");
            int vCount = 2;
            for (Venue v : venues) {
                System.out.println("  " + vCount + ") " + v.getVenueName());
                vCount++;
            }

            while (true) {
                System.out.print("Select new facility privilege (enter number, leave blank to skip): ");
                String pChoice = sc.nextLine().trim();
                if (pChoice.isEmpty())
                    break;

                try {
                    int pIdx = Integer.parseInt(pChoice);
                    if (pIdx == 1) {
                        target.setFacilityPrivilege("NONE");
                        changed = true;
                        break;
                    } else if (pIdx >= 2 && pIdx < vCount) {
                        int currentIdx = 2;
                        for (Venue v : venues) {
                            if (currentIdx == pIdx) {
                                target.setFacilityPrivilege(v.getVenueId());
                                changed = true;
                                break;
                            }
                            currentIdx++;
                        }
                        break;
                    } else {
                        System.out.println("Error: Invalid privilege number.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Error: Please enter a valid number.");
                }
            }
        } else {
            if (!"NONE".equals(target.getFacilityPrivilege())) {
                target.setFacilityPrivilege("NONE");
                changed = true;
            }
        }

        if (changed) {
            UserDatabase.saveUsers(users);
            System.out.println("User details updated successfully.");
            logHistory("USER_UPDATED", "ADMIN", "-", "Updated details for: " + target.getStudentId());
        } else {
            System.out.println("No changes made.");
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