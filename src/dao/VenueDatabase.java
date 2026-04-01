package dao;

import adt.VenueDQ;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import model.Venue;

/**
 * File-based CRUD for Venue entities.
 * File: venues.txt (CSV, one row per venue).
 */
public class VenueDatabase {

    private static final String FILE_NAME = "venues.txt";

    public static VenueDQ<Venue> loadVenues() {
        VenueDQ<Venue> venues = new VenueDQ<>();
        File file = new File(FILE_NAME);
        if (!file.exists()) return venues;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    Venue venue = Venue.fromFileString(line);
                    if (venue != null) {
                        venues.addLast(venue);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading venues: " + e.getMessage());
        }
        return venues;
    }

    public static void saveVenues(VenueDQ<Venue> venues) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_NAME, false))) {
            for (Venue venue : venues) {
                bw.write(venue.toFileString());
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving venues: " + e.getMessage());
        }
    }
}
