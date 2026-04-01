package control;

import utils.ValidationUtils;
import adt.LinkedDeque;
import adt.VenueDQ;
import dao.RegistrationDatabase;
import dao.VenueDatabase;
import model.Registration;
import model.Venue;

import java.util.Scanner;

/**
 * Register module — new user registration (sent to Admin for approval).

 *
 * @author CHEN XIANG HUI 

*/
public class RegisterModule {

    private final Scanner sc;

    public RegisterModule(Scanner sc) {
        this.sc = sc;
    }

    public void run() {
        System.out.println("\n===== NEW USER REGISTRATION =====");

        String name = ValidationUtils.readNonBlankString(sc, "Full Name (press Q to exit): ");
        if (name.equalsIgnoreCase("Q")) {
            return;
        }
        String id = ValidationUtils.readStudentId(sc, "Student ID (e.g. 21WMR12345): ");
        String message = ValidationUtils.readNonBlankString(sc, "Usage / Request Message: ");

        System.out.println("Role:");
        System.out.println("  1. Normal User");
        System.out.println("  2. Privileged User");
        System.out.print("Choice: ");
        String roleChoice = sc.nextLine().trim();
        String role;
        if ("2".equals(roleChoice)) {
            role = "PRIVILEGED_USER";
        } else {
            role = "NORMAL_USER";
        }

        String facilityPrivilege = "NONE";
        if ("PRIVILEGED_USER".equals(role)) {
            VenueDQ<Venue> venues = VenueDatabase.loadVenues();
            System.out.println("Facility Privilege:");
            System.out.println("  1. NONE");
            int vCount = 2;
            for (Venue v : venues) {
                System.out.println("  " + vCount + ". " + v.getVenueName());
                vCount++;
            }
            
            while (true) {
                System.out.print("Choice: ");
                String privChoice = sc.nextLine().trim();
                if (privChoice.isEmpty()) {
                    facilityPrivilege = "NONE";
                    break;
                }
                
                try {
                    int pIdx = Integer.parseInt(privChoice);
                    if (pIdx == 1) {
                        facilityPrivilege = "NONE";
                        break;
                    } else if (pIdx >= 2 && pIdx < vCount) {
                        int currentIdx = 2;
                        for (Venue v : venues) {
                            if (currentIdx == pIdx) {
                                facilityPrivilege = v.getVenueId();
                                break;
                            }
                            currentIdx++;
                        }
                        break;
                    } else {
                        System.out.println("Error: Invalid privilege number.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Error: Please enter a valid number.");
                }
            }
        }

        Registration reg = new Registration(name, id, message, role, facilityPrivilege, "PENDING");

        LinkedDeque<Registration> registrations = RegistrationDatabase.loadRegistrations();
        registrations.addLast(reg);
        RegistrationDatabase.saveRegistrations(registrations);

        System.out.println("\nRegistration submitted! Please wait for Admin approval.");
        System.out.println("Your ID: " + id);
    }

    public void viewRegistrationStatus() {
        System.out.println("\n========== SEARCH REGISTRATION/USER STATUS ==========");
        String studentId = ValidationUtils.readNonBlankString(sc, "Enter Student ID (e.g. 21WMR12345): ");
        
        // Find in registrations - O(n) Search
        LinkedDeque<Registration> registrations = RegistrationDatabase.loadRegistrations();
        Registration reg = null;
        for (Registration r : registrations) {
            if (r.getId().equals(studentId)) {
                reg = r;
            }
        }
        
        // Find in UserDatabase as well (in case they are suspended)
        adt.UserDQ users = dao.UserDatabase.loadUsers();
        model.User user = (model.User) users.find(studentId);
        
        if (reg == null && user == null) {
            System.out.println("No registration record found! Please proceed to registration!");
            return;
        }

        String name = user != null ? user.getName() : reg.getName();
        String status = user != null ? user.getStatus().name() : reg.getStatus();
        String role = user != null ? user.getRole().name() : reg.getRole();
        
        System.out.println("Student ID : " + studentId);
        System.out.println("Name       : " + name);
        System.out.println("Status     : " + status);
        System.out.println("Role       : " + role);
        
        switch (status) {
            case "APPROVED":
                System.out.println("You may now proceed to login.");
                break;
            case "PENDING":
                System.out.println("Your registration is still being processed and verified");
                break;
            case "REJECTED":
                System.out.println("Your registration has been rejected, please contact DSA for more info");
                break;
            case "SUSPENDED":
                System.out.println("Your account is suspended. Please contact DSA for more info");
                break;
        }
    }
}
