/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package control;

import adt.VenueDQ;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import model.Venue;
/**
 *
 * @author User
 */
public class VenueDatabase {

    private static final String FILE_NAME = "venues.txt";

    public static VenueDQ<Venue> loadVenues() {
        VenueDQ<Venue> venueList = new VenueDQ<>();
        File file = new File(FILE_NAME);

        if (!file.exists()) {
            return venueList;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;

            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    Venue venue = Venue.fromFileString(line);
                    if (venue != null) {
                        venueList.addLast(venue);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading venues: " + e.getMessage());
        }

        return venueList;
    }

    public static void saveVenues(VenueDQ<Venue> venueList) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (Venue venue : venueList) {
                bw.write(venue.toFileString());
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving venues: " + e.getMessage());
        }
    }
}
