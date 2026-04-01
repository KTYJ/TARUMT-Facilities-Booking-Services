package model;

/**
 * A flat history record tracking an action in the system.
 * Stored append-only in history.txt.
 */
public class History {

    private String timestamp;  // e.g. "2026-03-25 17:00"
    private String action;     // e.g. "BOOKED", "CANCELLED", "VENUE_CREATED"
    private String userId;
    private String venueId;
    private String details;    // free-text description

    public History() {
    }

    public History(String timestamp, String action, String userId,
                   String venueId, String details) {
        this.timestamp = timestamp;
        this.action = action;
        this.userId = userId;
        this.venueId = venueId;
        this.details = details;
    }

    // ---- Getters ----
    public String getTimestamp() { return timestamp; }
    public String getAction()   { return action; }
    public String getUserId()   { return userId; }
    public String getVenueId()  { return venueId; }
    public String getDetails()  { return details; }

    // ---- Setters ----
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
    public void setAction(String action)       { this.action = action; }
    public void setUserId(String userId)       { this.userId = userId; }
    public void setVenueId(String venueId)     { this.venueId = venueId; }
    public void setDetails(String details)     { this.details = details; }

    @Override
    public String toString() {
        return "[" + timestamp + "] " + action + " | User: " + userId
                + " | Venue: " + venueId + " | " + details;
    }

    /** CSV: timestamp,action,userId,venueId,details */
    public String toFileString() {
        return timestamp + "|" + action + "|" + userId + "|" + venueId + "|" + details;
    }

    public static History fromFileString(String line) {
        String[] p = line.split("\\|");
        if (p.length != 5) return null;
        return new History(p[0], p[1], p[2], p[3], p[4]);
    }
}