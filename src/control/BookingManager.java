package control;

import java.time.LocalDateTime;
import adt.BookingDQ;
import adt.LinkedDeque;
import model.AttendanceStatus;
import model.Booking;
import model.BookingParticipant;
import model.BookingStatus;
import model.User;
import model.UserRole;
import model.UserStatus;
import model.Venue;
import model.VenueStatus;

/**
 *
 * @author User
 */
public class BookingManager {

    private BookingDQ<Booking> bookingList;
    private int nextBookingNumber;

    private UserManager userManager;
    private VenueManager venueManager;

    private static final int GRACE_PERIOD_MINUTES = 15;

    public BookingManager(UserManager userManager, VenueManager venueManager) {
        this.bookingList = new BookingDQ<>();
        this.nextBookingNumber = 1;
        this.userManager = userManager;
        this.venueManager = venueManager;
    }

    public BookingDQ<Booking> getBookingList() {
        return bookingList;
    }

    private String generateBookingId() {
        String bookingId;

        if (nextBookingNumber < 10) {
            bookingId = "B00" + nextBookingNumber;
        } else if (nextBookingNumber < 100) {
            bookingId = "B0" + nextBookingNumber;
        } else {
            bookingId = "B" + nextBookingNumber;
        }

        nextBookingNumber++;
        return bookingId;
    }

    public Booking findBookingById(String bookingId) {
        return bookingList.find(bookingId);
    }

    // Check overlapping active booking for same venue
    public Booking findActiveBookingBySlot(String venueId, LocalDateTime start, LocalDateTime end) {
        for (Booking booking : bookingList) {
            if (booking.getVenueId().equals(venueId)
                    && booking.getBookingStatus() == BookingStatus.ACTIVE
                    && start.isBefore(booking.getSlotEndDateTime())
                    && booking.getSlotStartDateTime().isBefore(end)) {
                return booking;
            }
        }
        return null;
    }

    public Booking createBooking(String headUserId, String venueId,
            LocalDateTime slotStartDateTime,
            LocalDateTime slotEndDateTime,
            LinkedDeque<String> friendIds) {

        if (headUserId == null || venueId == null || slotStartDateTime == null
                || slotEndDateTime == null || friendIds == null) {
            return null;
        }

        // reject past booking
        if (!slotStartDateTime.isAfter(LocalDateTime.now())) {
            return null;
        }

        // end must be after start
        if (!slotEndDateTime.isAfter(slotStartDateTime)) {
            return null;
        }

        User headUser = userManager.findUserByStudentId(headUserId);
        Venue venue = venueManager.findVenueById(venueId);

        if (headUser == null || venue == null) {
            return null;
        }

        if (headUser.getStatus() != UserStatus.APPROVED) {
            return null;
        }

        if (venue.getStatus() == VenueStatus.BLOCKED || venue.getStatus() == VenueStatus.MAINTENANCE) {
            return null;
        }

        LinkedDeque<BookingParticipant> participants = new LinkedDeque<>();
        participants.addLast(new BookingParticipant(headUserId));

        LinkedDeque<String> tempFriendIds = copyStringDeque(friendIds);

        while (!tempFriendIds.isEmpty()) {
            String friendId = tempFriendIds.removeFirst();

            if (containsParticipantId(participants, friendId)) {
                return null;
            }

            User friend = userManager.findUserByStudentId(friendId);

            if (friend == null || friend.getStatus() != UserStatus.APPROVED) {
                return null;
            }

            participants.addLast(new BookingParticipant(friendId));
        }

        // head + friends must match venue capacity exactly
        if (participants.size() != venue.getCapacity()) {
            return null;
        }

        String bookingId = generateBookingId();
        Booking existingActive = findActiveBookingBySlot(venueId, slotStartDateTime, slotEndDateTime);

        // No conflict -> active booking
        if (existingActive == null) {
            Booking booking = new Booking(
                    bookingId,
                    headUserId,
                    venueId,
                    slotStartDateTime,
                    slotEndDateTime,
                    participants,
                    BookingStatus.ACTIVE
            );

            bookingList.addLast(booking);
            return booking;
        }

        // Conflict exists
        Booking newBooking = new Booking(
                bookingId,
                headUserId,
                venueId,
                slotStartDateTime,
                slotEndDateTime,
                participants,
                BookingStatus.WAITING
        );

        // Privileged user cuts the line and overrides old active booking
        if (headUser.getRole() == UserRole.PRIVILEGED_USER) {
            newBooking.setBookingStatus(BookingStatus.ACTIVE);

            // old active booking cancelled immediately
            existingActive.setBookingStatus(BookingStatus.CANCELLED);

            // old waiting queue transferred to new privileged active booking
            while (!existingActive.getWaitingQueue().isEmpty()) {
                newBooking.getWaitingQueue().addLast(existingActive.getWaitingQueue().removeFirst());
            }

            bookingList.addLast(newBooking);
            return newBooking;
        }

        // Normal user goes to waiting list
        existingActive.getWaitingQueue().addLast(newBooking);
        bookingList.addLast(newBooking);

        // if queue starts after grace period already passed, enforce immediately
        processForfeitureIfNeeded(existingActive, LocalDateTime.now());

        return newBooking;
    }

    public boolean markParticipantShowUp(String bookingId, String studentId) {
        Booking booking = findBookingById(bookingId);

        if (booking == null || booking.getBookingStatus() != BookingStatus.ACTIVE) {
            return false;
        }

        for (BookingParticipant participant : booking.getParticipants()) {
            if (participant.getStudentId().equals(studentId)) {
                participant.setAttendanceStatus(AttendanceStatus.SHOW_UP);
                return true;
            }
        }

        return false;
    }

    public boolean markParticipantNoShow(String bookingId, String studentId) {
        Booking booking = findBookingById(bookingId);

        if (booking == null || booking.getBookingStatus() != BookingStatus.ACTIVE) {
            return false;
        }

        for (BookingParticipant participant : booking.getParticipants()) {
            if (participant.getStudentId().equals(studentId)) {
                participant.setAttendanceStatus(AttendanceStatus.NO_SHOW);
                return true;
            }
        }

        return false;
    }

    public boolean areAllParticipantsShowUp(Booking booking) {
        LinkedDeque<BookingParticipant> temp = copyParticipantDeque(booking.getParticipants());

        while (!temp.isEmpty()) {
            BookingParticipant participant = temp.removeFirst();
            if (participant.getAttendanceStatus() != AttendanceStatus.SHOW_UP) {
                return false;
            }
        }

        return true;
    }

    public Booking processForfeitureIfNeeded(String bookingId, LocalDateTime currentDateTime) {
        Booking booking = findBookingById(bookingId);

        if (booking == null) {
            return null;
        }

        return processForfeitureIfNeeded(booking, currentDateTime);
    }

    private Booking processForfeitureIfNeeded(Booking booking, LocalDateTime currentDateTime) {
        if (booking.getBookingStatus() != BookingStatus.ACTIVE) {
            return null;
        }

        // no queue behind -> do not forfeit
        if (booking.getWaitingQueue().isEmpty()) {
            return null;
        }

        LocalDateTime graceDeadline = booking.getSlotStartDateTime().plusMinutes(GRACE_PERIOD_MINUTES);

        if (currentDateTime.isBefore(graceDeadline)) {
            return null;
        }

        if (areAllParticipantsShowUp(booking)) {
            return null;
        }

        booking.setBookingStatus(BookingStatus.FORFEITED);

        Booking promotedBooking = booking.getWaitingQueue().removeFirst();
        promotedBooking.setBookingStatus(BookingStatus.ACTIVE);

        while (!booking.getWaitingQueue().isEmpty()) {
            promotedBooking.getWaitingQueue().addLast(booking.getWaitingQueue().removeFirst());
        }

        return promotedBooking;
    }

    public boolean cancelBooking(String bookingId) {
        Booking booking = findBookingById(bookingId);

        if (booking == null) {
            return false;
        }

        booking.setBookingStatus(BookingStatus.CANCELLED);

        if (!booking.getWaitingQueue().isEmpty()) {
            Booking promotedBooking = booking.getWaitingQueue().removeFirst();
            promotedBooking.setBookingStatus(BookingStatus.ACTIVE);

            while (!booking.getWaitingQueue().isEmpty()) {
                promotedBooking.getWaitingQueue().addLast(booking.getWaitingQueue().removeFirst());
            }
        }

        return true;
    }

    // user can only cancel own booking
    public boolean cancelBookingByUser(String bookingId, String studentId) {
        Booking booking = findBookingById(bookingId);

        if (booking == null) {
            return false;
        }

        if (!booking.getHeadUserId().equals(studentId)) {
            return false;
        }

        return cancelBooking(bookingId);
    }

    public void checkAllActiveBookingsForForfeiture(LocalDateTime currentDateTime) {
        for (Booking booking : bookingList) {
            if (booking.getBookingStatus() == BookingStatus.ACTIVE) {
                processForfeitureIfNeeded(booking, currentDateTime);
            }
        }
    }

    // cancel all bookings by suspended user
    public void cancelBookingsByStudentId(String studentId) {
        for (Booking booking : bookingList) {
            if (booking.getHeadUserId().equals(studentId)
                    && (booking.getBookingStatus() == BookingStatus.ACTIVE
                    || booking.getBookingStatus() == BookingStatus.WAITING)) {
                cancelBooking(booking.getBookingId());
            }
        }
    }

    // cancel all bookings of blocked venue
    public void cancelBookingsByVenueId(String venueId) {
        for (Booking booking : bookingList) {
            if (booking.getVenueId().equals(venueId)
                    && (booking.getBookingStatus() == BookingStatus.ACTIVE
                    || booking.getBookingStatus() == BookingStatus.WAITING)) {
                booking.setBookingStatus(BookingStatus.CANCELLED);
            }
        }
    }

    public void displayAllBookings() {
        if (bookingList.isEmpty()) {
            System.out.println("No bookings found.");
            return;
        }

        for (Booking booking : bookingList) {
            System.out.println(booking);
        }
    }

    public void displayBookingsByStudentId(String studentId) {
        boolean found = false;

        for (Booking booking : bookingList) {
            if (booking.getHeadUserId().equals(studentId)) {
                System.out.println(booking);
                found = true;
            }
        }

        if (!found) {
            System.out.println("No booking history found.");
        }
    }

    public void displayActiveBookings() {
        boolean found = false;

        for (Booking booking : bookingList) {
            if (booking.getBookingStatus() == BookingStatus.ACTIVE) {
                System.out.println(booking);
                found = true;
            }
        }

        if (!found) {
            System.out.println("No active bookings found.");
        }
    }

    public void displayWaitingBookings() {
        boolean found = false;

        for (Booking booking : bookingList) {
            if (booking.getBookingStatus() == BookingStatus.WAITING) {
                System.out.println(booking);
                found = true;
            }
        }

        if (!found) {
            System.out.println("No waiting bookings found.");
        }
    }

    public void displayParticipants(String bookingId) {
        Booking booking = findBookingById(bookingId);

        if (booking == null) {
            System.out.println("Booking not found.");
            return;
        }

        LinkedDeque<BookingParticipant> temp = copyParticipantDeque(booking.getParticipants());

        while (!temp.isEmpty()) {
            System.out.println(temp.removeFirst());
        }
    }

    public void displayBookingsByVenue(String venueId) {
        boolean found = false;

        for (Booking booking : bookingList) {
            if (booking.getVenueId().equals(venueId)) {
                System.out.println(booking);
                found = true;
            }
        }

        if (!found) {
            System.out.println("No bookings found for the venue " + venueId);
        }
    }

    public void displayWaitingQueueForBooking(String bookingId) {
        Booking booking = findBookingById(bookingId);

        if (booking == null) {
            System.out.println("Booking not found.");
            return;
        }

        if (booking.getWaitingQueue().isEmpty()) {
            System.out.println("No waiting queue for booking " + bookingId);
            return;
        }

        for (Booking waitingBooking : booking.getWaitingQueue()) {
            System.out.println(waitingBooking);
        }
    }

    // ---------- helper methods ----------
    private boolean containsParticipantId(LinkedDeque<BookingParticipant> participants, String studentId) {
        LinkedDeque<BookingParticipant> temp = copyParticipantDeque(participants);

        while (!temp.isEmpty()) {
            BookingParticipant participant = temp.removeFirst();
            if (participant.getStudentId().equals(studentId)) {
                return true;
            }
        }

        return false;
    }

    private LinkedDeque<String> copyStringDeque(LinkedDeque<String> original) {
        LinkedDeque<String> copy = new LinkedDeque<>();
        LinkedDeque<String> temp = new LinkedDeque<>();

        while (!original.isEmpty()) {
            String value = original.removeFirst();
            copy.addLast(value);
            temp.addLast(value);
        }

        while (!temp.isEmpty()) {
            original.addLast(temp.removeFirst());
        }

        return copy;
    }

    private LinkedDeque<BookingParticipant> copyParticipantDeque(LinkedDeque<BookingParticipant> original) {
        LinkedDeque<BookingParticipant> copy = new LinkedDeque<>();
        LinkedDeque<BookingParticipant> temp = new LinkedDeque<>();

        while (!original.isEmpty()) {
            BookingParticipant value = original.removeFirst();
            copy.addLast(value);
            temp.addLast(value);
        }

        while (!temp.isEmpty()) {
            original.addLast(temp.removeFirst());
        }

        return copy;
    }
}
