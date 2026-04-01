package dao;

import adt.LinkedDeque;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import model.History;

/**
 * File-based CRUD for History log entries.
 * File: history.txt (pipe-delimited, one row per entry).
 */
public class HistoryDatabase {

    private static final String FILE_NAME = "history.txt";

    public static LinkedDeque<History> loadHistory() {
        LinkedDeque<History> history = new LinkedDeque<>();
        File file = new File(FILE_NAME);
        if (!file.exists()) return history;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    History h = History.fromFileString(line);
                    if (h != null) {
                        history.addLast(h);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading history: " + e.getMessage());
        }
        return history;
    }

    public static void saveHistory(LinkedDeque<History> history) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_NAME, false))) {
            for (History h : history) {
                bw.write(h.toFileString());
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving history: " + e.getMessage());
        }
    }
}
