/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author CHEN XIANG HUI
 */
/**
 * A registration request submitted by a new user. Sent to Admin for approval
 * before the user can log in.
 */
public class Registration {

    private String name;
    private String id;
    private String message;           // usage / request message
    private String role;              // "NORMAL_USER" or "PRIVILEGED_USER"
    private String facilityPrivilege; // e.g. "FENCING_ROOM", "ALL", "NONE"
    private String status;            // "PENDING", "APPROVED", "REJECTED"

    public Registration() {
    }

    public Registration(String name, String id, String message,
            String role, String facilityPrivilege, String status) {
        this.name = name;
        this.id = id;
        this.message = message;
        this.role = role;
        this.facilityPrivilege = facilityPrivilege;
        this.status = status;
    }

    // ---- Getters ----
    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    public String getRole() {
        return role;
    }

    public String getFacilityPrivilege() {
        return facilityPrivilege;
    }

    public String getStatus() {
        return status;
    }

    // ---- Setters ----
    public void setName(String name) {
        this.name = name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setFacilityPrivilege(String facilityPrivilege) {
        this.facilityPrivilege = facilityPrivilege;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return String.format(
                "Registration [ID: %s | Name: %s | Role: %s | Privilege: %s | Status: %s | Msg: '%s']",
                id, name, role, facilityPrivilege, status, message
        );
    }

    /**
     * CSV: name,id,message,role,facilityPrivilege,status
     */
    public String toFileString() {
        return name + "," + id + "," + message + ","
                + role + "," + facilityPrivilege + "," + status;
    }

    public static Registration fromFileString(String line) {
        String[] p = line.split(",");
        if (p.length != 6) {
            return null;
        }
        return new Registration(p[0], p[1], p[2], p[3], p[4], p[5]);
    }
}
