/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author KTYJ
 */
public class Student extends User {

    public Student(String id, String password, String name) {
        super(id, password, name);
    }

    public void bookFacility(Venue venue, TimeSlot slot) {}

    public void joinWaitlist(Venue venue, TimeSlot slot) {}

    public void viewSlots(Venue venue) {}
}
