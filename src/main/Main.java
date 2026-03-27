package main;

import adt.UserDQ;
import control.AdminModule;
import control.PrivilegedModule;
import control.RegisterModule;
import control.StudentModule;
import dao.UserDatabase;
import model.User;
import model.UserStatus;

import java.util.Scanner;

public class Main {

    static String logo = """                                     
  ::::::::. .::.       :.::;;:.  :::   .::.:::.  .:::.::::::::: 
  :::;;:::. :;;;.   ::;;:. .:;;  ;;;   :;:.;;;:. .;;;:.:::;;::: 
     ;;:   .;::;:.  .:;:  ..:;:  ;;;   :;:.;;;;:.:;;;.   :;;    
     ;;:  .;:. :;:  .:;::;;:.    ;;;   :;:.;;;;::;;;;.   :;;    
     ;;:  :;;;;;;;. .:;: .:;:    ;;;   :;:.;;::;;;::;.   :;;    
     ;;: .;:.   :;:..::.   :;:   .;;;:;;;. ;;:.;;:.:;.   :;;    
                            .:;:    ...                         
                              .:;:         .                    
                                 .::::::..                                                         
""";

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int choice;

        do {
            System.out.print(Main.logo);
            System.out.println("        FACILITY BOOKING SYSTEM       \n");
            System.out.println("=======================================");
            System.out.println("|  1. Admin Login                     |");
            System.out.println("|  2. Student Login                   |");
            System.out.println("|  3. Privileged User Login           |");
            System.out.println("|  4. Register New Account            |");
            System.out.println("|  0. Exit                            |");
            System.out.println("=======================================");

            System.out.print("Choice: ");
            choice = readInt(sc);

            switch (choice) {
                case 1 ->
                    adminLogin(sc);
                case 2 ->
                    new StudentModule(sc).loginAndRun();
                case 3 ->
                    new PrivilegedModule(sc).loginAndRun();
                case 4 ->
                    new RegisterModule(sc).run();
                case 0 ->
                    System.out.println("Goodbye!");
                default ->
                    System.out.println("Invalid choice.");
            }
        } while (choice != 0);

        sc.close();
    }

    /**
     * Simple admin login — hardcoded check against users.txt
     */
    private static void adminLogin(Scanner sc) {
        String id = control.ValidationUtils.readUserId(sc, "Admin ID: ");
        String pw = control.ValidationUtils.readNonBlankString(sc, "Password: ");

        UserDQ<User> users = UserDatabase.loadUsers();
        User u = (User) users.find(id);
        if (u == null) {
            System.out.println("Admin not found.");
            return;
        }
        if (u.getStatus() != UserStatus.APPROVED) {
            System.out.println("Account not approved.");
            return;
        }
        if (!u.isAdmin()) {
            System.out.println("Not an admin account.");
            return;
        }
        if (!u.getPassword().equals(pw)) {
            System.out.println("Wrong password.");
            return;
        }

        System.out.println("Welcome, " + u.getName() + "!");
        new AdminModule(sc).run();
    }

    private static int readInt(Scanner sc) {
        return control.ValidationUtils.readInt(sc, "");
    }
}
