/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author KTYJ
 */
public class PrivilegedUser extends User {

    private String[] facilityPrivileges;

    public PrivilegedUser(String id, String password, String name) {
        super(id, password, name);
    }

    public void expressBooking(Venue venue, TimeSlot slot) {}

    public void viewSlots(Venue venue) {}
}
