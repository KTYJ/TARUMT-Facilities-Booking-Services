package dao;

import adt.LinkedDeque;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import model.Registration;

/**
 * File-based CRUD for Registration entities.
 * File: registrations.txt (CSV, one row per registration).
 */
public class RegistrationDatabase {

    private static final String FILE_NAME = "registrations.txt";

    public static LinkedDeque<Registration> loadRegistrations() {
        LinkedDeque<Registration> registrations = new LinkedDeque<>();
        File file = new File(FILE_NAME);
        if (!file.exists()) return registrations;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    Registration reg = Registration.fromFileString(line);
                    if (reg != null) {
                        registrations.addLast(reg);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading registrations: " + e.getMessage());
        }
        return registrations;
    }

    public static void saveRegistrations(LinkedDeque<Registration> registrations) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_NAME, false))) {
            for (Registration reg : registrations) {
                bw.write(reg.toFileString());
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving registrations: " + e.getMessage());
        }
    }
}
