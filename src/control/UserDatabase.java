/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package control;

import adt.UserDQ;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import model.User;
/**
 *
 * @author User
 */
public class UserDatabase {
    private static final String USER_DATABASE = "users.txt";
    
    public static UserDQ<User> loadUsers(){
        UserDQ<User> users = new UserDQ<>();
        File file = new File(USER_DATABASE);
        
        if(!file.exists()){
            return users;
        }
        
        try (BufferedReader br = new BufferedReader(new FileReader(file))){
            String line;
            
            while((line = br.readLine()) != null){
                if(!line.trim().isEmpty()){
                    User user = User.fromFileString(line);
                    if (user !=null){
                        users.addLast(user);
                    }
                }
            }
        } catch (IOException e){
            System.out.println("Error loading users from file:" + e.getMessage());
        }
        
        return users;
    }
    
    public static void saveUsers(UserDQ<User> users){
        try (BufferedWriter bw = new BufferedWriter (new FileWriter(USER_DATABASE))){
            for (User user : users){
                bw.write(user.toFileString());
                bw.newLine();
            }
        } catch (IOException e){
            System.out.println("Error saving users to file:" + e.getMessage());
        }
    }
}
