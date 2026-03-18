/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author User
 */
public class Venue {
    private String venueId;
    private String venueName;
    private VenueStatus status;
    private int capacity;
    
    public Venue(){
    }
    
    public Venue(String venueId, String venueName, VenueStatus status, int capacity) {
        this.venueId = venueId;
        this.venueName = venueName;
        this.status = status;
        this.capacity = capacity;
    }
    
    public Venue(String venueId, String venueName, int capacity){
        this.venueId = venueId;
        this.venueName = venueName;
        this.status = VenueStatus.AVAILABLE; //default available
        this.capacity = capacity;
    }
    
    public String getId() {
        return venueId;
    }
    
    public String getVenueName(){
        return venueName;
    }
    
    public VenueStatus getStatus() {
        return status;
    }
    
    public int getCapacity(){
        return capacity;
    }
    
    public void setVenueId(String venueId){
        this.venueId = venueId;
    }
    
    public void setVenueName(String venueName){
        this.venueName = venueName;
    }
    
    public void setStatus(VenueStatus status){
        this.status = status;
    }
    
    public void setCapacity(int capacity){
        this.capacity = capacity;
    }
    
    public boolean isAvailable(){
        return status == VenueStatus.AVAILABLE;
    }
    
    @Override
    public String toString(){
        return "Venue{" +
                "id='" + venueId + '\'' +
                ", venueName='" + venueName + '\'' +
                ", status=" + status +
                ", capacity=" + capacity +
                '}';
    }
}
