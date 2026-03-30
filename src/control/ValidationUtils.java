package control;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

/**
 * Utility class for common input validations.
 */
public class ValidationUtils {

    /**
     * Reads a non-blank string from the user.
     */
    public static String readNonBlankString(Scanner sc, String prompt) {
        String input;
        while (true) {
            System.out.print(prompt);
            input = sc.nextLine().trim();
            if (!input.isEmpty()) {
                return input;
            }
            System.out.println("Error: Input cannot be empty. Please try again.");
            System.out.print("> ");
        }
    }

    /**
     * Reads a valid Student ID (Format: nnXXXnnNnn, e.g., 21WMR12345).
     * Regex: 2 digits, 3 letters, 5 digits.
     */
    public static String readStudentId(Scanner sc, String prompt) {
        String id;
        while (true) {
            id = readNonBlankString(sc, prompt);
            if (id.matches("^\\d{2}[a-zA-Z]{3}\\d{5}$")) {
                return id.toUpperCase();
            }
            System.out.println(
                    "Error: Invalid ID format. Must be 2 digits, 3 letters, 4 digits (e.g. 21WMR12345).");
            System.out.print("> ");

        }
    }

    /**
     * Reads a valid Admin/Privileged ID (non-empty). Note: Admin IDs may not follow
     * student format.
     */
    public static String readUserId(Scanner sc, String prompt) {
        return readNonBlankString(sc, prompt);
    }

    /**
     * Reads a valid Date in yyyy-MM-dd format.
     */
    public static String readDate(Scanner sc, String prompt) {
        String dateStr;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        while (true) {
            dateStr = readNonBlankString(sc, prompt);
            try {
                LocalDate.parse(dateStr, formatter);
                return dateStr;
            } catch (DateTimeParseException e) {
                System.out.println("Error: Invalid date format. Please use yyyy-MM-dd (e.g., 2026-03-25).");
                System.out.print("> ");
            }
        }
    }

    /**
     * Reads a valid Time in HH:mm format.
     */
    public static String readTime(Scanner sc, String prompt) {
        String timeStr;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        while (true) {
            timeStr = readNonBlankString(sc, prompt);
            try {
                LocalTime.parse(timeStr, formatter);
                return timeStr;
            } catch (DateTimeParseException e) {
                System.out.println("Error: Invalid time format. Please use HH:mm (e.g., 09:00, 14:30).");
                System.out.print("> ");
            }
        }
    }

    /**
     * Reads a valid integer.
     */
    public static int readInt(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = sc.nextLine().trim();
            try {
                return Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.println("Error: Please enter a valid number.");
                System.out.print("> ");
            }
        }
    }
}
