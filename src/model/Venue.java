/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author User
 */
public class Venue {
    private String venueId;
    private String venueName;
    private VenueStatus status;
    private int capacity;

    public Venue() {
    }

    public Venue(String venueId, String venueName, VenueStatus status, int capacity) {
        this.venueId = venueId;
        this.venueName = venueName;
        this.status = status;
        this.capacity = capacity;
    }

    public Venue(String venueId, String venueName, int capacity) {
        this.venueId = venueId;
        this.venueName = venueName;
        this.status = VenueStatus.AVAILABLE; // default available
        this.capacity = capacity;
    }

    public String getVenueId() {
        return venueId;
    }

    public String getVenueName() {
        return venueName;
    }

    public static boolean isValidVenueId(String id) {
        if (id == null || id.length() != 5) {
            return false;
        }
        if (!Character.isLetter(id.charAt(0))) {
            return false;
        }
        for (int i = 1; i < 5; i++) {
            if (!Character.isDigit(id.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public VenueStatus getStatus() {
        return status;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setVenueId(String venueId) {
        this.venueId = venueId;
    }

    public void setVenueName(String venueName) {
        this.venueName = venueName;
    }

    public void setStatus(VenueStatus status) {
        this.status = status;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public boolean isAvailable() {
        return status == VenueStatus.AVAILABLE;
    }

    @Override
    public String toString() {
        return venueId + " - " + venueName +
                " [" + status +
                " | Max: " + capacity + "]";
    }

    public String toFileString() {
        return venueId + ","
                + venueName + ","
                + status + ","
                + capacity;
    }

    public static Venue fromFileString(String line) {
        String[] parts = line.split(",");

        if (parts.length != 4) {
            return null;
        }

        String venueId = parts[0];
        String venueName = parts[1];
        VenueStatus status = VenueStatus.valueOf(parts[2].trim());
        int capacity = Integer.parseInt(parts[3]);

        return new Venue(venueId, venueName, status, capacity);
    }
}
