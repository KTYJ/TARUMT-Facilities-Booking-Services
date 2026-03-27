package control;

import adt.LinkedDeque;
import dao.RegistrationDatabase;
import model.Registration;

import java.util.Scanner;

/**
 * Register module — new user registration (sent to Admin for approval).
 */
public class RegisterModule {

    private final Scanner sc;

    public RegisterModule(Scanner sc) {
        this.sc = sc;
    }

    public void run() {
        System.out.println("\n===== NEW USER REGISTRATION =====");

        String name = ValidationUtils.readNonBlankString(sc, "Full Name: ");
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
            System.out.println("Facility Privilege:");
            System.out.println("  1. Fencing Room");
            System.out.println("  2. Study Room");
            System.out.println("  3. Sound Room");
            System.out.println("  4. All Facilities");
            System.out.print("Choice: ");
            String privChoice = sc.nextLine().trim();
            switch (privChoice) {
                case "1" -> facilityPrivilege = "FENCING_ROOM";
                case "2" -> facilityPrivilege = "STUDY_ROOM";
                case "3" -> facilityPrivilege = "SOUND_ROOM";
                case "4" -> facilityPrivilege = "ALL";
                default  -> facilityPrivilege = "NONE";
            }
        }

        Registration reg = new Registration(name, id, message, role, facilityPrivilege, "PENDING");

        LinkedDeque<Registration> registrations = RegistrationDatabase.loadRegistrations();
        registrations.addLast(reg);
        RegistrationDatabase.saveRegistrations(registrations);

        System.out.println("\nRegistration submitted! Please wait for Admin approval.");
        System.out.println("Your ID: " + id);
    }
}
