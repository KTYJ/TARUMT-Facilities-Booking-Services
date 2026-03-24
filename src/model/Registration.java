package model;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author KTYJ
 */
public class Registration {

    private String name;
    private String id;
    private String message;
    private String role;
    private String facilityPrivilege;

    public Registration(String name, String id, String message, String role, String facilityPrivilege) {
        this.name = name;
        this.id = id;
        this.message = message;
        this.role = role;
        this.facilityPrivilege = facilityPrivilege;
    }
}
