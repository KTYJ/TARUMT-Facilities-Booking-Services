/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author KTYJ
 */
public class Venue {

    private String venueId;
    private String venueName;
    private boolean blocked;

    public Venue(String venueId, String venueName) {
        this.venueId = venueId;
        this.venueName = venueName;
        this.blocked = false;
    }

    public void blockVenue() {
        blocked = true;
    }

    public void unblockVenue() {
        blocked = false;
    }

    public boolean isBlocked() {
        return blocked;
    }
}
