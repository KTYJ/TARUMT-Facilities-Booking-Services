/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

/**
 *
 * @author WONG KAH LOK
 */
import adt.BookingDQ;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import model.Booking;

/**
 * File-based CRUD for Booking entities.
 * File: bookings.txt (CSV, one row per booking).
 */
public class BookingDatabase {

    private static final String FILE_NAME = "bookings.txt";

    public static BookingDQ<Booking> loadBookings() {
        BookingDQ<Booking> bookings = new BookingDQ<>();
        File file = new File(FILE_NAME);
        if (!file.exists()) return bookings;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    Booking booking = Booking.fromFileString(line);
                    if (booking != null) {
                        bookings.addLast(booking);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading bookings: " + e.getMessage());
        }
        return bookings;
    }

    public static void saveBookings(BookingDQ<Booking> bookings) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_NAME, false))) {
            for (Booking booking : bookings) {
                bw.write(booking.toFileString());
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving bookings: " + e.getMessage());
        }
    }
}
