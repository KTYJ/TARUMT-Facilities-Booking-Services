package dao;

import adt.UserDQ;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import model.User;

/**
 * File-based CRUD for User entities.
 * File: users.txt (CSV, one row per user).
 */
public class UserDatabase {

    private static final String FILE_NAME = "users.txt";

    /** Loads all users from file into a UserDQ. */
    public static UserDQ loadUsers() {
        UserDQ users = new UserDQ();
        File file = new File(FILE_NAME);
        if (!file.exists()) return users;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    User user = User.fromFileString(line);
                    if (user != null) {
                        users.addLast(user);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading users: " + e.getMessage());
        }
        return users;
    }

    /** Overwrites the file with the full deque contents. */
    public static void saveUsers(UserDQ users) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_NAME, false))) {
            for (User user : users) {
                bw.write(user.toFileString());
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving users: " + e.getMessage());
        }
    }
}
