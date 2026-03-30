package model;

/**
 * Represents a facility booking or waitlist entry.
 * Waitlisted bookings have status = WAITING.
 */
public class Booking {

    private String bookingId;
    private String userId;
    private String venueId;
    private String date;       // e.g. "2026-03-25"
    private String startTime;  // e.g. "09:00"
    private String endTime;    // e.g. "11:00"
    private BookingStatus bookingStatus;

    public Booking() {
        this.bookingStatus = BookingStatus.ACTIVE;
    }

    public Booking(String bookingId, String userId, String venueId,
                   String date, String startTime, String endTime,
                   BookingStatus bookingStatus) {
        this.bookingId = bookingId;
        this.userId = userId;
        this.venueId = venueId;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.bookingStatus = bookingStatus;
    }

    // ---- Getters ----
    public String getBookingId()          { return bookingId; }
    public String getUserId()             { return userId; }
    public String getVenueId()            { return venueId; }
    public String getDate()               { return date; }
    public String getStartTime()          { return startTime; }
    public String getEndTime()            { return endTime; }
    public BookingStatus getBookingStatus() { return bookingStatus; }

    // ---- Setters ----
    public void setBookingId(String bookingId)              { this.bookingId = bookingId; }
    public void setUserId(String userId)                    { this.userId = userId; }
    public void setVenueId(String venueId)                  { this.venueId = venueId; }
    public void setDate(String date)                        { this.date = date; }
    public void setStartTime(String startTime)              { this.startTime = startTime; }
    public void setEndTime(String endTime)                  { this.endTime = endTime; }
    public void setBookingStatus(BookingStatus bookingStatus) { this.bookingStatus = bookingStatus; }

    /** Checks if this booking occupies the same slot. */
    public boolean isSameSlot(String venueId, String date, String startTime, String endTime) {
        return this.venueId.equals(venueId)
                && this.date.equals(date)
                && this.startTime.equals(startTime)
                && this.endTime.equals(endTime);
    }

    @Override
    public String toString() {
        return String.format("| %-9s | %-10s | %-7s | %-10s | %s-%s | %-9s |",
                bookingId, userId, venueId, date, startTime, endTime, bookingStatus);
    }

    /** CSV: bookingId,userId,venueId,date,startTime,endTime,status */
    public String toFileString() {
        return bookingId + "," + userId + "," + venueId + ","
                + date + "," + startTime + "," + endTime + ","
                + bookingStatus;
    }

    public static Booking fromFileString(String line) {
        String[] p = line.split(",");
        if (p.length != 7) return null;
        return new Booking(p[0], p[1], p[2], p[3], p[4], p[5],
                BookingStatus.valueOf(p[6].trim()));
    }
}
