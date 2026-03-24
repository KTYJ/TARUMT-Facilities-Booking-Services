/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author User
 */
public class User {
    private String studentId;
    private String password;
    private String name;
    private UserRole role;
    private UserStatus status;
 
    
    public User() {
    }
    
    public User(String studentId, String password, String name, UserRole role, UserStatus status){
        this.studentId = studentId;
        this.password = password;
        this.name = name;
        this.role = role;
        this.status = status;
    }
    
    public User(String studentId, String password, String name) {
        this.studentId = studentId;
        this.password = password;
        this.name = name;
        this.role = UserRole.NORMAL_USER;
        this.status = UserStatus.PENDING;
    }

    
    public String getStudentId(){
        return studentId;
    }
    
    public String getPassword(){
        return password;
    }
    
    public String getName(){
        return name;
    }
    
    public UserRole getRole(){
        return role;
    }
    
    public UserStatus getStatus(){
        return status;
    }
    
    public void setStudentId(String studentId){
        this.studentId = studentId;
    }
    
    public void setPassword(String password){
        this.password = password;
    }
    
    public void setName(String name){
        this.name = name;
    }
    
    public void setRole(UserRole role){
        this.role = role;
    }
    
    public void setStatus(UserStatus status){
        this.status = status;
    }

    
    public boolean isPrivilegedUser(){
        return role == UserRole.PRIVILEGED_USER;
    }
    
    @Override
    public String toString(){
        return "User{" + "studentId='" + studentId + '\'' + ", name='" + name + '\'' + ", role=" + role + ", status=" + status + "}";
    }
    public String toFileString(){
        return studentId + "," +
                password + "," +
                name + "," +
                role + "," +
                status;
    
    }
    
    public static User fromFileString(String line){
        String[] parts = line.split(",");
        
        if(parts.length !=5){
            return null;
        }
        
        String studentId = parts[0];
        String password = parts[1];
        String name = parts[2];
        UserRole role = UserRole.valueOf(parts[3]);
        UserStatus status = UserStatus.valueOf(parts[4]);
        
        return new User(studentId, password, name, role, status);
        
    }
}
