/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package control;

import adt.UserDQ;
import model.User;
import model.UserRole;
import model.UserStatus;
/**
 *
 * @author User
 */  
public class UserManager {
    private UserDQ<User> userList;
    
    public UserManager(){
        userList = new UserDQ<>();
    }
    
    public UserDQ<User> getUserList(){
        return userList;
    }
    
    public boolean registerUser(User user) {
        if (user == null){
            return false;
        }
        
        if (findUserByStudentId(user.getStudentId()) != null){
            return false;
        }
        
        userList.addLast(user);
        return true;
    }
    public User findUserByStudentId(String studentId){
        return userList.find(studentId);
    }
    
    //Admin default adding in
    public void ensureDefaultAdminExists(){
        User existingAdmin = findUserByStudentId("ADMIN001");
        
        if( existingAdmin == null){
            User adminUser = new User("ADMIN001","admin123","System Admin");
            adminUser.setRole(UserRole.ADMIN);
            adminUser.setStatus(UserStatus.APPROVED);
            userList.addLast(adminUser);
            UserDatabase.saveUsers(userList);
        }
    }
    
    //login
    public User login(String studentId, String password){
        User user = findUserByStudentId(studentId);
        
        if (user != null && user.getPassword().equals(password)){
            return user;
        }
        return null;
    }
    
    //approve user login
    public boolean approveUser(String studentId){
        User user = findUserByStudentId(studentId);
        
        if (user != null){
            user.setStatus(UserStatus.APPROVED);
            UserDatabase.saveUsers(userList);
            return true;   
        }
        return false;
    }
    
    //reject user
    public boolean rejectUser(String studentId){
        User user = findUserByStudentId(studentId);
        
        if (user != null){
            user.setStatus(UserStatus.REJECTED);
            UserDatabase.saveUsers(userList);
            return true;
        }
        return false;
    }
    
    //suspend user
    public boolean suspendUser(String studentId) {
        User user = findUserByStudentId(studentId);

        if (user != null){
            user.setStatus(UserStatus.SUSPENDED);
            UserDatabase.saveUsers(userList);
            return true;
        }
        return false;
    }
    
    //To privileged user
    public boolean makePrivilegedUser(String studentId){
        User user = findUserByStudentId(studentId);
        
        if (user != null){
            user.setRole(UserRole.PRIVILEGED_USER);
            UserDatabase.saveUsers(userList);
            return true;
        }
        return false;
    }
    //back to normal
    public boolean makeNormalUser(String studentId){
        User user = findUserByStudentId(studentId);
        
        if (user != null){
            user.setRole(UserRole.NORMAL_USER);
            UserDatabase.saveUsers(userList);
            return true;
        }
        return false;
    }
    
    //check if user is approved
    public boolean isApprovedUser(String studentId){
        User user = findUserByStudentId(studentId);
        return user != null && user.getStatus() == UserStatus.APPROVED;
    }
    
    //check if user is admin
    public boolean isAdmin(String studentId){
        User user = findUserByStudentId(studentId);
        return user != null && user.getRole() == UserRole.ADMIN;
    }
    
    //display all users
    public void displayAllUsers(){
        if (userList.isEmpty()){
            System.out.println("No users found.");
            return;
            }
        for (User user: userList){
            System.out.println(user);
        }
    }
    //display pending users
    public void displayPendingUsers(){
        boolean found = false;
        
        for (User user : userList){
            if (user.getStatus() == UserStatus.PENDING){
                System.out.println(user);
                found = true;
            }
        }
        if (!found){
            System.out.println("No PENDING users found.");
        }
    }
    
    //display approved users
    public void displayApprovedUsers(){
        boolean found = false;
        
        for (User user : userList){
            if (user.getStatus() == UserStatus.APPROVED){
                System.out.println(user);
                found = true;
            }
        }
        if (!found){
            System.out.println("No APPROVED users found.");
        }
    }
    
    //display rejected users
    public void displayRejectedUsers(){
        boolean found = false;
        
        for (User user : userList){
            if (user.getStatus() == UserStatus.REJECTED){
                System.out.println(user);
                found = true;
            }
        }
        if (!found){
            System.out.println("No REJECTED users found.");
        }
    }
    
    //display suspended users
    public void displaySuspendedUsers(){
        boolean found = false;
        
        for (User user : userList){
            if (user.getStatus() == UserStatus.SUSPENDED){
                System.out.println(user);
                found = true;
            }
        }
        if (!found){
            System.out.println("No SUSPENDED users found.");
        }
    }
    
    
    //display user but sorted by ID
    public void sortUsersByStudentId() {
    int n = userList.size();

        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                User currentUser = userList.get(j);
                User nextUser = userList.get(j + 1);

                if (currentUser.getStudentId().compareToIgnoreCase(nextUser.getStudentId()) > 0) {
                    userList.set(j, nextUser);
                    userList.set(j + 1, currentUser);
                }
            }
        }
    }
    
    public void displayUsersSortedByStudentId() {
        if (userList.isEmpty()) {
            System.out.println("No users found.");
            return;
        }

        sortUsersByStudentId();

        System.out.println("\n===== USERS SORTED BY STUDENT ID =====");
        for (User user : userList) {
            System.out.println(user);
        }
    }
    
    //displaay user but sorted by name
    public void sortUsersByName() {
        int n = userList.size();

        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                User currentUser = userList.get(j);
                User nextUser = userList.get(j + 1);

                if (currentUser.getName().compareToIgnoreCase(nextUser.getName()) > 0) {
                    userList.set(j, nextUser);
                    userList.set(j + 1, currentUser);
                }
            }
        }
    }
    
    public void displayUsersSortedByName() {
        if (userList.isEmpty()) {
            System.out.println("No users found.");
            return;
        }

        sortUsersByName();

        System.out.println("\n===== USERS SORTED BY NAME =====");
        for (User user : userList) {
            System.out.println(user);
        }
    }
    //display normal users
    public void displayNormalUsers(){
        boolean found = false;
        
        for (User user : userList){
            if (user.getRole() == UserRole.NORMAL_USER){
                System.out.println(user);
                found = true;
            }
        }
        if (!found){
            System.out.println("No NORMAL users found.");
        }
    }
    
    //display privileged users
    public void displayPrivilegedUsers(){
        boolean found = false;
        
        for (User user : userList){
            if (user.getRole() == UserRole.PRIVILEGED_USER){
                System.out.println(user);
                found = true;
            }
        }
        if (!found){
            System.out.println("No PRIVILEGED users found.");
        }
    }
    
    //display admin users
    public void displayAdmins(){
        boolean found = false;
        
        for (User user : userList){
            if (user.getRole() == UserRole.ADMIN){
                System.out.println(user);
                found = true;
            }
        }
        if (!found){
            System.out.println("No ADMIN found.");
        }
    }
    
    //count all users
    public int getTotalUsers(){
        return userList.size();
    }
    
    //count users by status
    public int countUserByStatus(UserStatus status){
        int count = 0;
        
        for (User user : userList){
            if (user.getStatus() == status){
                count++;
            }
        }
        return count;
    }
    
    //count users by roles
    public int countUserByRole(UserRole role){
        int count = 0;
        
        for (User user : userList){
            if (user.getRole() == role){
                count++;
            }
        }
        return count;
    }    
}    
