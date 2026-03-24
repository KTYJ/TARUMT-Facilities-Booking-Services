/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author KTYJ
 */
import adt.BookingDQ;
import adt.LinkedDeque;
import java.time.LocalDateTime;

public class Booking {

    private String bookingId;
    private String headUserId;
    private String venueId;

    private LocalDateTime slotStartDateTime;
    private LocalDateTime slotEndDateTime;

    private LinkedDeque<BookingParticipant> participants;
    private BookingStatus bookingStatus;

    // queue of waiting teams for the same slot
    private BookingDQ<Booking> waitingQueue;

    public Booking() {
        participants = new LinkedDeque<>();
        waitingQueue = new BookingDQ<>();
        bookingStatus = BookingStatus.ACTIVE;
    }

    public Booking(String bookingId, String headUserId, String venueId,
            LocalDateTime slotStartDateTime, LocalDateTime slotEndDateTime,
            LinkedDeque<BookingParticipant> participants,
            BookingStatus bookingStatus) {
        this.bookingId = bookingId;
        this.headUserId = headUserId;
        this.venueId = venueId;
        this.slotStartDateTime = slotStartDateTime;
        this.slotEndDateTime = slotEndDateTime;
        this.participants = participants;
        this.bookingStatus = bookingStatus;
        this.waitingQueue = new BookingDQ<>();
    }

    public String getBookingId() {
        return bookingId;
    }

    public String getHeadUserId() {
        return headUserId;
    }

    public String getVenueId() {
        return venueId;
    }

    public LocalDateTime getSlotStartDateTime() {
        return slotStartDateTime;
    }

    public LocalDateTime getSlotEndDateTime() {
        return slotEndDateTime;
    }

    public LinkedDeque<BookingParticipant> getParticipants() {
        return participants;
    }

    public BookingStatus getBookingStatus() {
        return bookingStatus;
    }

    public BookingDQ<Booking> getWaitingQueue() {
        return waitingQueue;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public void setHeadUserId(String headUserId) {
        this.headUserId = headUserId;
    }

    public void setVenueId(String venueId) {
        this.venueId = venueId;
    }

    public void setSlotStartDateTime(LocalDateTime slotStartDateTime) {
        this.slotStartDateTime = slotStartDateTime;
    }

    public void setSlotEndDateTime(LocalDateTime slotEndDateTime) {
        this.slotEndDateTime = slotEndDateTime;
    }

    public void setParticipants(LinkedDeque<BookingParticipant> participants) {
        this.participants = participants;
    }

    public void setBookingStatus(BookingStatus bookingStatus) {
        this.bookingStatus = bookingStatus;
    }

    public boolean isSameSlot(String venueId, LocalDateTime start, LocalDateTime end) {
        return this.venueId.equals(venueId)
                && this.slotStartDateTime.equals(start)
                && this.slotEndDateTime.equals(end);
    }

    public int getParticipantCount() {
        return participants.size();
    }

    @Override
    public String toString() {
        return "Booking{"
                + "bookingId='" + bookingId + '\''
                + ", headUserId='" + headUserId + '\''
                + ", venueId='" + venueId + '\''
                + ", slotStartDateTime=" + slotStartDateTime
                + ", slotEndDateTime=" + slotEndDateTime
                + ", participantCount=" + participants.size()
                + ", bookingStatus=" + bookingStatus
                + ", waitingQueueSize=" + waitingQueue.size()
                + '}';
    }
}
