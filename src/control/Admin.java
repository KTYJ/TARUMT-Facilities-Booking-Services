/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package control;

import java.time.LocalDateTime;
import model.Booking;
import model.User;
import model.UserRole;
import model.UserStatus;
import model.Venue;
import model.VenueStatus;
/**
 *
 * @author User
 */
public class Admin {
    private UserManager userManager;
    private VenueManager venueManager;
    private BookingManager bookingManager;
    
    public Admin(UserManager userManager, VenueManager venueManager, BookingManager bookingManager){
        this.userManager = userManager;
        this.venueManager = venueManager;
        this.bookingManager = bookingManager;
    }
    
    //User Management
    public boolean approveUser(String studentId){
        return userManager.approveUser(studentId);
    }
    
    public boolean rejectUser(String studentId){
        return userManager.rejectUser(studentId);
    }
    
    public boolean suspendUser(String studentId){
        boolean success = userManager.suspendUser(studentId);
        
        if(success){
            bookingManager.cancelBookingsByStudentId(studentId);
        }
        return success;
    }
    
    public boolean makePrivilegedUser(String studentId){
        return userManager.makePrivilegedUser(studentId);
    }
    
    public boolean makeNormalUser(String studentId){
        return userManager.makeNormalUser(studentId);
    }
    
    public void displayAllUsers(){
        userManager.displayAllUsers();
    }
    
    public void displayPendingUsers(){
        userManager.displayPendingUsers();
    }
    
    public void displayApprovedUsers(){
        userManager.displayApprovedUsers();
    }
    
    public void displayRejectedUsers(){
        userManager.displayRejectedUsers();
    }
    
    public void displaySuspendedUsers(){
        userManager.displaySuspendedUsers();
    }
    
    public void displayPrivilegedUsers(){
        userManager.displayPrivilegedUsers();
    }
    
    public void displayAdmins(){
        userManager.displayAdmins();
    }
    
    //Venue Management
    public Venue createVenue(String venueName, int capacity){
        return venueManager.createVenue(venueName, capacity);
    }
    
    public boolean updateVenueName(String venueId, String newVenueName){
        return venueManager.updateVenueName(venueId, newVenueName);
    }
    
    public boolean updateVenueCapacity(String venueId, int newVenueCapacity){
        return venueManager.updateVenueCapacity(venueId, newVenueCapacity);
    }
    
    public boolean updateVenueStatus(String venueId, VenueStatus status){
        return venueManager.updateVenueStatus(venueId, status);
    }
    
    public boolean blockVenue(String venueId) {
        boolean success = venueManager.blockVenue(venueId);

        if (success) {
            bookingManager.cancelBookingsByVenueId(venueId);
        }

        return success;
    }

    public boolean setVenueAvailable(String venueId) {
        return venueManager.setVenueAvailable(venueId);
    }

    public boolean setVenueMaintenance(String venueId) {
        return venueManager.setVenueMaintenance(venueId);
    }

    public boolean setVenueBooked(String venueId) {
        return venueManager.setVenueBooked(venueId);
    }

    public boolean deleteVenue(String venueId) {
        return venueManager.deleteVenue(venueId);
    }

    public void displayAllVenues() {
        venueManager.displayAllVenues();
    }

    public void displayAvailableVenues() {
        venueManager.displayAvailableVenues();
    }

    public void displayBlockedVenues() {
        venueManager.displayBlockedVenues();
    }

    public void displayMaintenanceVenues() {
        venueManager.displayMaintenanceVenues();
    }

    public void displayBookedVenues() {
        venueManager.displayBookedVenues();
    }
    
    //booking management
    public boolean markParticipantShowUp(String bookingId, String studentId){
        return bookingManager.markParticipantShowUp(bookingId, studentId);
    }
    
    public boolean markParticipantNoShow(String bookingId, String studentId){
        return bookingManager.markParticipantNoShow(bookingId, studentId);
    }
    
public Booking processForfeitureIfNeeded(String bookingId, LocalDateTime currentDateTime) {
        return bookingManager.processForfeitureIfNeeded(bookingId, currentDateTime);
    }

    public void checkAllActiveBookingsForForfeiture(LocalDateTime currentDateTime) {
        bookingManager.checkAllActiveBookingsForForfeiture(currentDateTime);
    }

    public boolean cancelBooking(String bookingId) {
        return bookingManager.cancelBooking(bookingId);
    }

    public void displayAllBookings() {
        bookingManager.displayAllBookings();
    }

    public void displayActiveBookings() {
        bookingManager.displayActiveBookings();
    }

    public void displayWaitingBookings() {
        bookingManager.displayWaitingBookings();
    }

    public void displayBookingsByVenue(String venueId) {
        bookingManager.displayBookingsByVenue(venueId);
    }

    public void displayParticipants(String bookingId) {
        bookingManager.displayParticipants(bookingId);
    }

    public void displayWaitingQueueForBooking(String bookingId) {
        bookingManager.displayWaitingQueueForBooking(bookingId);
    }

    // ---------------- SEARCH HELPERS ----------------

    public User findUserByStudentId(String studentId) {
        return userManager.findUserByStudentId(studentId);
    }

    public Venue findVenueById(String venueId) {
        return venueManager.findVenueById(venueId);
    }

    public Booking findBookingById(String bookingId) {
        return bookingManager.findBookingById(bookingId);
    }
}
