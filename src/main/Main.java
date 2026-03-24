/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package main;

import adt.LinkedDeque;
import control.Admin;
import control.BookingManager;
import control.UserManager;
import control.VenueManager;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Scanner;
import model.Booking;
import model.User;
import model.UserRole;
import model.UserStatus;
import model.Venue;
/**
 *
 * @author User
 */
public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    
    private static UserManager userManager = new UserManager();
    private static VenueManager venueManager = new VenueManager();
    private static BookingManager bookingManager = new BookingManager(userManager, venueManager);
    private static Admin admin = new Admin (userManager, venueManager, bookingManager);
    
    public static void main(String[]args){
        userManager.ensureDefaultAdminExists();
        
        int choice;
        do{
            System.out.println("\n==================================================");
            System.out.println("    TARUMT FACILITY BOOKING SYSTEM - MAIN MENU");
            System.out.println("==================================================");
            System.out.println("1. Register");
            System.out.println("2. View Registration Status Board");
            System.out.println("3. User Login");
            System.out.println("4. Admin Login");
            System.out.println("0. Exit");
            System.out.println("==================================================");
            System.out.println("Enter Choice:");
            choice = readInt();
            
            switch(choice){
            case 1:
                registerUser();
                break;
            case 2:
                registrationStatusBoardMenu();
                break;
            case 3:
                userLogin();
                break;
            case 4:
                adminLogin();
                break;
            case 0:
                System.out.println("Exited.");
            default:
                System.out.println("Invalid Choice. Pick the right number!");
            }
        } while (choice !=0);
    }
    
    //Registration
    private static void registerUser(){
        System.out.println("\n**********USER REGISTRATION**********");
        System.out.println("Enter Student ID (Format:12abc34567) : ");
        String studentId = scanner.nextLine().trim();
        
        //Validate Format
        if(!studentId.matches("\\d{2}[A-Z]{3}\\d{5}")){
            System.out.println("Invalid Student ID format, please try again !");
            return;
        }
        
        if(studentId.isEmpty()){
            System.out.println("Student ID is empty, please enter your ID!");
            return;
        }
        
        if (userManager.findUserByStudentId(studentId) != null){
            System.out.println("This student ID is already registered, please enter your unique student ID!");
            return;
        }
        
        System.out.print("Enter Name:");
        String name = scanner.nextLine().trim();
        
        System.out.print("Enter Password: ");
        String password = scanner.nextLine().trim();
        
        if(name.isEmpty() || password.isEmpty()){
            System.out.println("Please provide all information");
            return;
        }
        
        System.out.println("Select Registration Type:");
        System.out.println("1. Normal User");
        System.out.println("2. Privileged User Request");
        System.out.print("Enter choice: ");
        int roleChoice = readInt();
        
        User user = new User(studentId, password, name);
        
        if (roleChoice == 2) {
            user.setRole(UserRole.PRIVILEGED_USER);
        } else {
            user.setRole(UserRole.NORMAL_USER);
        }

        user.setStatus(UserStatus.PENDING);
        
        boolean success = userManager.registerUser(user);
        
        if (success){
            System.out.println("registration Successful.");
            System.out.println("Your account is now PENDING for approval.");
            
            if (user.getRole() == UserRole.PRIVILEGED_USER) {
                System.out.println("Your privileged user request will be reviewed by admin.");
            }
            
            System.out.println("Please check the registration status page for your status");
        } else{
            System.out.println("Registration failed. Please try again.");
        }
    }
    
    //registration status board menu for all
    private static void registrationStatusBoardMenu(){
        int choice;
        
        do {
            System.out.println("\n==================================================");
            System.out.println("         REGISTRATION STATUS BOARD");
            System.out.println("==================================================");
            System.out.println("1. View Full Status Board");
            System.out.println("2. Search Status By Student ID");
            System.out.println("0. Back");
            System.out.println("==================================================");
            System.out.print("Enter your choice: ");
            choice = readInt();
            
            switch (choice){
                case 1:
                    displayRegistrationStatusBoard();
                    break;
                case 2:
                    searchRegistrationStatusByStudentId();
                    break;
                case 0:
                    System.out.println("Returning to main menu.");
                    break;
                default:
                    System.out.println("Invalid choice, pick a valid number");
            }
        } while (choice !=0);
    }
    
    private static void displayRegistrationStatusBoard(){
        System.out.println("\n==================================================");
        System.out.println("           REGISTRATION STATUS BOARD");
        System.out.println("==================================================");
        
        System.out.println("\n----- APPROVED USERS -----");
        admin.displayApprovedUsers();
        
        System.out.println("\n----- PENDING USERS -----");
        admin.displayPendingUsers();
        
        System.out.println("\n----- REJECTED USERS -----");
        admin.displayRejectedUsers();
        
        System.out.println("\n----- Suspended USERS -----");
        admin.displaySuspendedUsers();
    }
    
    private static void searchRegistrationStatusByStudentId(){
        System.out.println("\n========== SEARCH REGISTRATION STATUS ==========");
        System.out.println("Enter tudent ID");
        String studentId = scanner.nextLine().trim();
        
        User user = userManager.findUserByStudentId(studentId);
        
        if(user == null){
            System.out.println("No registration record found");
            return;
        }
        
        System.out.println("Student ID : " + user.getStudentId());
        System.out.println("Name       : " + user.getName());
        System.out.println("Status     : " + user.getStatus());
        System.out.println("ROle       : " + user.getRole());
        
        if(user.getStatus() == UserStatus.APPROVED){
            System.out.println("You may now proceed to login.");
        } else if(user.getStatus() == UserStatus.PENDING){
            System.out.println("Your registration is still being processed and verified");
        } else if(user.getStatus() == UserStatus.REJECTED){
            System.out.println("Your registration has been rejected, please contact DSA for more info");
        } else if(user.getStatus() == UserStatus.SUSPENDED){
            System.out.println("Your account is suspended");
        }
    }
    
    //User Login
    private static void userLogin(){
        System.out.println("\n========== USER LOGIN ==========");
        System.out.print("Enter Student ID: ");
        String studentId = scanner.nextLine().trim();
        
        System.out.print("Enter Password: ");
        String password = scanner.nextLine().trim();
        
        User user = userManager.findUserByStudentId(studentId);
        
        if (user == null){
            System.out.println("User does not exist");
            return;
        }
        
        if(!user.getPassword().equals(password)){
            System.out.println("Incorrect Password");
            return;
        }
        
        if(user.getStatus() != UserStatus.APPROVED){
            System.out.println("You are not allowed to login yet.");
            System.out.println("Current Status "+ user.getStatus());
            return;
        }
        
        if(user.getRole() == UserRole.ADMIN){
            System.out.println("Wrong portal, Please use the Admin Login");
            return;
        }
        
        System.out.println("Login successful. Welcome" + user.getName() + "!");
        userMenu(user);
    }
    
    private static void userMenu(User currentUser){
        int choice;
        
        do {
            System.out.println("\n==================================================");
            System.out.println("               USER MENU");
            System.out.println("==================================================");
            System.out.println("1. View My Profile");
            System.out.println("2. View Available Venues");
            System.out.println("3. Book a venue");
            System.out.println("4. View My Booking History");
            System.out.println("5. View Registration Status Board");
            System.out.println("0. Logout");
            System.out.println("==================================================");
            System.out.print("Enter choice: ");
            choice = readInt();
            
            switch(choice){
                case 1:
                    System.out.println("Student ID : " + currentUser.getStudentId());
                    System.out.println("Name       : " + currentUser.getName());
                    System.out.println("Role       : " + currentUser.getRole());
                    System.out.println("Status     : " + currentUser.getStatus());
                    break;
                    
                case 2:
                admin.displayAvailableVenues();
                break;
                
                case 3:
                createBookingForUser(currentUser);
                break;
                
                case 4:
                bookingManager.displayBookingsByStudentId(currentUser.getStudentId());
                break;
                
                case 5:
                registrationStatusBoardMenu();
                
                case 0:
                    System.out.println("Logged out.");
                    break;
                    
                default:
                    System.out.println("Invalid choice");
            }
        } while (choice !=0);
    }
    
    //user booking
    private static void createBookingForUser(User currentUser){
        System.out.println("\n========== CREATE BOOKING ===========");
        admin.displayAvailableVenues();
        
        System.out.print("Enter Venue ID: ");
        String venueId = scanner.nextLine().trim();
        
        Venue venue = venueManager.findVenueById(venueId);
        if (venue == null){
            System.out.println("Venue not found.");
            return;
        }
        
        System.out.println("Selected venue capacity: " + venue.getCapacity());
        
        try{
            System.out.println("Enter booking date (yyyy-MM-dd): ");
            String dateInput = scanner.nextLine().trim();
            
            java.time.LocalDate bookingDate = java.time.LocalDate.parse(dateInput);

            LocalDateTime start = chooseStartTime(bookingDate);
            LocalDateTime end = chooseEndTime(bookingDate);
            
            if (end.isBefore(start) || end.equals(start)) {
                System.out.println("End time must be after start time.");
                return;
            }
            
            int friendsNeeded = venue.getCapacity() -1; //headuser ignored
            LinkedDeque<String> friendIds = new LinkedDeque<>();
            
            if (friendsNeeded > 0){
                System.out.println("You must enter" + friendsNeeded + "friend student IDs.");
            }
            
            for (int i = 1;i< friendsNeeded;i++){
                System.out.print("Enter friend student ID" + i + ":");
                String friendId = scanner.nextLine().trim();
                friendIds.addLast(friendId);
            }
            
            Booking booking = bookingManager.createBooking(
                    currentUser.getStudentId(),
                    venueId,
                    start,
                    end,
                    friendIds
            );
            
            if (booking == null){
                System.out.println("Booking failed.");
                System.out.println("Possible reasons:");
                System.out.println("* slot is in the past");
                System.out.println("* venue is blocked or under maintenance");
                System.out.println("* capacity does not match");
                System.out.println("* one or more users are not approved");
                System.out.println("* duplicate participant IDs");
            }else {
                System.out.println("Booking created successfully.");
                System.out.println(booking);
            }
        }catch (DateTimeParseException e){
            System.out.println("Invalid date-time format.");
        }
    }
    
    //Admin Login
    private static void adminLogin(){
        System.out.println("\n========== ADMIN LOGIN ==========");
        System.out.println("Enter Admin ID: ");
        String adminId = scanner.nextLine().trim();
        
        System.out.print("Enter Password:");
        String password = scanner.nextLine().trim();
        
        User adminUser = userManager.findUserByStudentId(adminId);
                
        if (adminUser == null){
            System.out.println("Admin account not found.");
            return;
        }
        
        if(!adminUser.getPassword().equals(password)){
            System.out.println("Incorrect password.");
            return;
        }
        
       if(adminUser.getRole() !=UserRole.ADMIN){
           System.out.println("This account is not an admin account! Try with a valid account!");
           return;
       }
       
       if(adminUser.getStatus() !=UserStatus.APPROVED) {
           System.out.println("Admin account is inactive");
           return;
       }
       
       System.out.println("Admin Login Successful");
       adminMenu();
    }
    
    //Admin Menu
    private static void adminMenu(){
        int choice;
        
        do{
            System.out.println("\n==================================================");
            System.out.println("                 ADMIN MENU");
            System.out.println("==================================================");
            System.out.println("1. View Pending Users");
            System.out.println("2. Approve User");
            System.out.println("3. Reject User");
            System.out.println("4. Suspend User");
            System.out.println("5. View Registration Status Board");
            System.out.println("6. Create Venue");
            System.out.println("7. View All Venues");
            System.out.println("8. Block Venue");
            System.out.println("9. Set Venue Available");
            System.out.println("10. Set Venue Maintenance");
            System.out.println("11. View All Bookings");
            System.out.println("12. View Waiting Bookings");
            System.out.println("13. View Bookings By Venue");
            System.out.println("14. View Booking Participants");
            System.out.println("15. Mark Participant SHOW_UP");
            System.out.println("16. Mark Participant NO_SHOW");
            System.out.println("17. Process Forfeiture For One Booking");
            System.out.println("18. Check All Active Bookings For Forfeiture");
            System.out.println("19. View Users Sorted By Student ID");
            System.out.println("20. View Users Sorted By Name");
            System.out.println("0. Logout");
            System.out.println("==================================================");
            System.out.print("Enter choice: ");
            choice = readInt();
            
            switch (choice) {
                case 1:
                    admin.displayPendingUsers();
                    break;

                case 2:
                    System.out.print("Enter Student ID to approve: ");
                    String approveId = scanner.nextLine().trim();
                    System.out.println(admin.approveUser(approveId) ? "User approved." : "Approve failed.");
                    break;

                case 3:
                    System.out.print("Enter Student ID to reject: ");
                    String rejectId = scanner.nextLine().trim();
                    System.out.println(admin.rejectUser(rejectId) ? "User rejected." : "Reject failed.");
                    break;

                case 4:
                    System.out.print("Enter Student ID to suspend: ");
                    String suspendId = scanner.nextLine().trim();
                    System.out.println(admin.suspendUser(suspendId) ? "User suspended." : "Suspend failed.");
                    break;

                case 5:
                    registrationStatusBoardMenu();
                    break;

                case 6:
                    createVenueByAdmin();
                    break;

                case 7:
                    admin.displayAllVenues();
                    break;

                case 8:
                    System.out.print("Enter Venue ID to block: ");
                    String blockVenueId = scanner.nextLine().trim();
                    System.out.println(admin.blockVenue(blockVenueId) ? "Venue blocked." : "Block failed.");
                    break;

                case 9:
                    System.out.print("Enter Venue ID to set available: ");
                    String availableVenueId = scanner.nextLine().trim();
                    System.out.println(admin.setVenueAvailable(availableVenueId) ? "Venue set to AVAILABLE." : "Update failed.");
                    break;

                case 10:
                    System.out.print("Enter Venue ID to set maintenance: ");
                    String maintenanceVenueId = scanner.nextLine().trim();
                    System.out.println(admin.setVenueMaintenance(maintenanceVenueId) ? "Venue set to MAINTENANCE." : "Update failed.");
                    break;

                case 11:
                    admin.displayAllBookings();
                    break;

                case 12:
                    admin.displayWaitingBookings();
                    break;

                case 13:
                    System.out.print("Enter Venue ID: ");
                    String bookingVenueId = scanner.nextLine().trim();
                    admin.displayBookingsByVenue(bookingVenueId);
                    break;

                case 14:
                    System.out.print("Enter Booking ID: ");
                    String bookingIdForParticipants = scanner.nextLine().trim();
                    admin.displayParticipants(bookingIdForParticipants);
                    break;

                case 15:
                    System.out.print("Enter Booking ID: ");
                    String bookingIdShow = scanner.nextLine().trim();
                    System.out.print("Enter Student ID: ");
                    String studentIdShow = scanner.nextLine().trim();
                    System.out.println(admin.markParticipantShowUp(bookingIdShow, studentIdShow)
                            ? "Participant marked SHOW_UP."
                            : "Mark failed.");
                    break;

                case 16:
                    System.out.print("Enter Booking ID: ");
                    String bookingIdNoShow = scanner.nextLine().trim();
                    System.out.print("Enter Student ID: ");
                    String studentIdNoShow = scanner.nextLine().trim();
                    System.out.println(admin.markParticipantNoShow(bookingIdNoShow, studentIdNoShow)
                            ? "Participant marked NO_SHOW."
                            : "Mark failed.");
                    break;

                case 17:
                    processSingleForfeiture();
                    break;

                case 18:
                    admin.checkAllActiveBookingsForForfeiture(LocalDateTime.now());
                    System.out.println("All active bookings checked.");
                    break;
                    
                case 19:
                    userManager.displayUsersSortedByStudentId();
                    break;
                    
                case 20:
                    userManager.displayUsersSortedByName();
                    break;

                case 0:
                    System.out.println("Admin logged out.");
                    break;

                default:
                    System.out.println("Invalid choice.");
            }
        } while (choice != 0);
    }

    // ==================================================
    // ADMIN HELPERS
    // ==================================================
    private static void createVenueByAdmin() {
        System.out.println("\n========== CREATE VENUE ==========");
        System.out.print("Enter Venue Name: ");
        String venueName = scanner.nextLine().trim();

        System.out.print("Enter Capacity: ");
        int capacity = readInt();

        Venue venue = admin.createVenue(venueName, capacity);

        if (venue == null) {
            System.out.println("Venue creation failed.");
        } else {
            System.out.println("Venue created successfully.");
            System.out.println(venue);
        }
    }

    private static void processSingleForfeiture() {
        System.out.print("Enter Booking ID to check forfeiture: ");
        String bookingId = scanner.nextLine().trim();

        Booking promotedBooking = admin.processForfeitureIfNeeded(bookingId, LocalDateTime.now());

        if (promotedBooking == null) {
            System.out.println("No forfeiture happened.");
        } else {
            System.out.println("Booking forfeited. Promoted booking:");
            System.out.println(promotedBooking);
        }
    }

    // ==================================================
    // INPUT HELPER
    // ==================================================
    
    private static LocalDateTime chooseStartTime(java.time.LocalDate bookingDate) {
        System.out.println("\nChoose Start Time:");
        System.out.println("1. 08:00");
        System.out.println("2. 09:00");
        System.out.println("3. 10:00");
        System.out.println("4. 11:00");
        System.out.println("5. 12:00");
        System.out.println("6. 13:00");
        System.out.println("7. 14:00");
        System.out.println("8. 15:00");
        System.out.println("9. 16:00");
        System.out.println("10. 17:00");
        System.out.println("11. 18:00");
        System.out.println("12. 19:00");
        System.out.println("13. 20:00");
        System.out.print("Enter choice: ");
        

        int choice = readInt();

        switch (choice) {
            case 1:
                return bookingDate.atTime(8, 0);
            case 2:
                return bookingDate.atTime(9, 0);
            case 3:
                return bookingDate.atTime(10, 0);
            case 4:
                return bookingDate.atTime(11, 0);
            case 5:
                return bookingDate.atTime(12, 0);
            case 6:
                return bookingDate.atTime(13, 0);
            case 7:
                return bookingDate.atTime(14, 0);
            case 8:
                return bookingDate.atTime(15, 0);
            case 9:
                return bookingDate.atTime(16, 0);
            case 10:
                return bookingDate.atTime(17, 0);
            case 11:
                return bookingDate.atTime(18, 0);
            case 12:
                return bookingDate.atTime(19, 0);
            case 13:
                return bookingDate.atTime(20, 0);
                
            default:
                System.out.println("Invalid choice. Defaulting to 08:00.");
                return bookingDate.atTime(8, 0);
        }
    }
    
    private static LocalDateTime chooseEndTime(java.time.LocalDate bookingDate) {
        System.out.println("\nChoose End Time:");
        System.out.println("1. 09:00");
        System.out.println("2. 10:00");
        System.out.println("3. 11:00");
        System.out.println("4. 12:00");
        System.out.println("5. 13:00");
        System.out.println("6. 14:00");
        System.out.println("7. 15:00");
        System.out.println("8. 16:00");
        System.out.println("9. 17:00");
        System.out.println("10. 18:00");
        System.out.print("Enter choice: ");

        int choice = readInt();

        switch (choice) {
            case 1:
                return bookingDate.atTime(9, 0);
            case 2:
                return bookingDate.atTime(10, 0);
            case 3:
                return bookingDate.atTime(11, 0);
            case 4:
                return bookingDate.atTime(12, 0);
            case 5:
                return bookingDate.atTime(13, 0);
            case 6:
                return bookingDate.atTime(14, 0);
            case 7:
                return bookingDate.atTime(15, 0);
            case 8:
                return bookingDate.atTime(16, 0);
            case 9:
                return bookingDate.atTime(17, 0);
            case 10:
                return bookingDate.atTime(18, 0);
            case 11:
                return bookingDate.atTime(19, 0);
            case 12:
                return bookingDate.atTime(20, 0);
            case 13:
                return bookingDate.atTime(21, 0);
            default:
                System.out.println("Invalid choice. Defaulting to 09:00.");
                return bookingDate.atTime(9, 0);
        }
    }
    
    private static int readInt() {
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.print("Invalid number. Enter again: ");
            }
        }
    }
}