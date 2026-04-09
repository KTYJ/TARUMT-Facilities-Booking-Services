/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author WONG KAH LOK
 */
import model.status.UserRole;
import model.status.UserStatus;

/**
 * Represents any user in the system (Student, Privileged, or Admin).
 * Role is determined by the UserRole enum; no subclasses needed.
 */
public class User {

    private String studentId;
    private String password;
    private String name;
    private UserRole role;
    private UserStatus status;
    private String facilityPrivilege; // e.g. "FENCING_ROOM", "ALL", "NONE"

    public User() {
    }

    public User(String studentId, String password, String name,
                UserRole role, UserStatus status, String facilityPrivilege) {
        this.studentId = studentId;
        this.password = password;
        this.name = name;
        this.role = role;
        this.status = status;
        this.facilityPrivilege = facilityPrivilege;
    }

    public User(String studentId, String password, String name) {
        this(studentId, password, name, UserRole.NORMAL_USER, UserStatus.PENDING, "NONE");
    }

    //  Getters 
    public String getStudentId()        { return studentId; }
    public String getPassword()         { return password; }
    public String getName()             { return name; }
    public UserRole getRole()           { return role; }
    public UserStatus getStatus()       { return status; }
    public String getFacilityPrivilege(){ return facilityPrivilege; }

    //  Setters 
    public void setStudentId(String studentId)              { this.studentId = studentId; }
    public void setPassword(String password)                { this.password = password; }
    public void setName(String name)                        { this.name = name; }
    public void setRole(UserRole role)                      { this.role = role; }
    public void setStatus(UserStatus status)                { this.status = status; }
    public void setFacilityPrivilege(String facilityPrivilege) { this.facilityPrivilege = facilityPrivilege; }

    // ---- Helpers ----
    public boolean isPrivilegedUser() { return role == UserRole.PRIVILEGED_USER; }
    public boolean isAdmin()          { return role == UserRole.ADMIN; }

    @Override
    public String toString() {
        return "[" + studentId + "] " + name
                + " - " + role + " - (" + status
                + ") (Privilege: " + facilityPrivilege + ")";
    }


    
    /** CSV: studentId,password,name,role,status,facilityPrivilege */
    public String toFileString() {
        return studentId + "," + password + "," + name + ","
                + role + "," + status + "," + facilityPrivilege;
    }

    public static User fromFileString(String line) {
        String[] p = line.split(",");
        if (p.length != 6) return null;
        return new User(p[0], p[1], p[2],
                UserRole.valueOf(p[3]),
                UserStatus.valueOf(p[4]),
                p[5]);
    }
}
