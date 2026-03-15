/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author KTYJ
 */
public class Admin extends User {

    public Admin(String id, String password, String name) {
        super(id, password, name);
    }

    public void createVenue(Venue venue) {}

    public void updateVenue(Venue venue) {}

    public void removeVenue(String venueId) {}

    public void blockVenue(String venueId, String reason) {}

    public void searchVenue(String keyword) {}

    public void acceptRegistration(Registration r) {}

    public void removeFromWaitlist(User user, Venue venue) {}

    public void viewSlots(Venue venue) {}

    public void viewRegisteredUsers() {}

    public void viewHistory() {}
}
