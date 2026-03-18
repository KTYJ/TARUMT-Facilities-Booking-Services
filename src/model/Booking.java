/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author KTYJ
 */
public class Booking {

    private String bookingId;
    private User user;
    private Venue venue;
    private TimeSlot slot;
    private boolean 

    public Booking(String bookingId, User user, Venue venue, TimeSlot slot) {
        this.bookingId = bookingId;
        this.user = user;
        this.venue = venue;
        this.slot = slot;
        this
    }

    public String getBookingId() {
        return bookingId;
    }
}