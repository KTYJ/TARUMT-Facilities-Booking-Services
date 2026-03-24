package control;

import adt.VenueDQ;
import model.Venue;
import model.VenueStatus;

public class VenueManager {

    private VenueDQ<Venue> venueList;
    private int nextVenueNumber;

    public VenueManager() {
        venueList = VenueDatabase.loadVenues();
        nextVenueNumber = getNextVenueNumberFromFile();
    }

    public VenueDQ<Venue> getVenueList() {
        return venueList;
    }

    private int getNextVenueNumberFromFile() {
        int max = 0;

        for (Venue venue : venueList) {
            String venueId = venue.getVenueId();   // Example: V001
            if (venueId != null && venueId.startsWith("V")) {
                try {
                    int num = Integer.parseInt(venueId.substring(1));
                    if (num > max) {
                        max = num;
                    }
                } catch (NumberFormatException e) {
                    // Ignore invalid venue IDs
                }
            }
        }

        return max + 1;
    }

    private String generateVenueId() {
        String venueId;

        if (nextVenueNumber < 10) {
            venueId = "V00" + nextVenueNumber;
        } else if (nextVenueNumber < 100) {
            venueId = "V0" + nextVenueNumber;
        } else {
            venueId = "V" + nextVenueNumber;
        }

        nextVenueNumber++;
        return venueId;
    }

    // create and add new venue
    public Venue createVenue(String venueName, int capacity) {
        if (venueName == null || venueName.trim().isEmpty() || capacity <= 0) {
            return null;
        }

        String newVenueId = generateVenueId();
        Venue venue = new Venue(newVenueId, venueName, capacity);
        venueList.addLast(venue);
        VenueDatabase.saveVenues(venueList);
        return venue;
    }

    public boolean addVenue(Venue venue) {
        if (venue == null) {
            return false;
        }

        if (findVenueById(venue.getVenueId()) != null) {
            return false;
        }

        venueList.addLast(venue);
        VenueDatabase.saveVenues(venueList);
        return true;
    }

    // find venue by id
    public Venue findVenueById(String venueId) {
        return venueList.find(venueId);
    }

    // update venue name
    public boolean updateVenueName(String venueId, String newVenueName) {
        Venue venue = findVenueById(venueId);

        if (venue != null && newVenueName != null && !newVenueName.trim().isEmpty()) {
            venue.setVenueName(newVenueName);
            VenueDatabase.saveVenues(venueList);
            return true;
        }
        return false;
    }

    // update venue capacity
    public boolean updateVenueCapacity(String venueId, int newCapacity) {
        Venue venue = findVenueById(venueId);

        if (venue != null && newCapacity > 0) {
            venue.setCapacity(newCapacity);
            VenueDatabase.saveVenues(venueList);
            return true;
        }
        return false;
    }

    // update venue status directly
    public boolean updateVenueStatus(String venueId, VenueStatus newStatus) {
        Venue venue = findVenueById(venueId);

        if (venue != null && newStatus != null) {
            venue.setStatus(newStatus);
            VenueDatabase.saveVenues(venueList);
            return true;
        }
        return false;
    }

    // block venue
    public boolean blockVenue(String venueId) {
        return updateVenueStatus(venueId, VenueStatus.BLOCKED);
    }

    // maintenance
    public boolean setVenueMaintenance(String venueId) {
        return updateVenueStatus(venueId, VenueStatus.MAINTENANCE);
    }

    // available
    public boolean setVenueAvailable(String venueId) {
        return updateVenueStatus(venueId, VenueStatus.AVAILABLE);
    }

    // booked
    public boolean setVenueBooked(String venueId) {
        return updateVenueStatus(venueId, VenueStatus.BOOKED);
    }

    // delete venue by rebuilding deque
    public boolean deleteVenue(String venueId) {
        if (findVenueById(venueId) == null) {
            return false;
        }

        VenueDQ<Venue> newVenueList = new VenueDQ<>();

        for (Venue venue : venueList) {
            if (!venue.getVenueId().equals(venueId)) {
                newVenueList.addLast(venue);
            }
        }

        venueList = newVenueList;
        VenueDatabase.saveVenues(venueList);
        return true;
    }

    // display all venues
    public void displayAllVenues() {
        if (venueList.isEmpty()) {
            System.out.println("No venues found.");
            return;
        }

        for (Venue venue : venueList) {
            System.out.println(venue);
        }
    }

    // display available venues
    public void displayAvailableVenues() {
        boolean found = false;

        for (Venue venue : venueList) {
            if (venue.getStatus() == VenueStatus.AVAILABLE) {
                System.out.println(venue);
                found = true;
            }
        }

        if (!found) {
            System.out.println("No available venues found");
        }
    }

    // display blocked venues
    public void displayBlockedVenues() {
        boolean found = false;

        for (Venue venue : venueList) {
            if (venue.getStatus() == VenueStatus.BLOCKED) {
                System.out.println(venue);
                found = true;
            }
        }

        if (!found) {
            System.out.println("No blocked venues found");
        }
    }

    // display maintenance venues
    public void displayMaintenanceVenues() {
        boolean found = false;

        for (Venue venue : venueList) {
            if (venue.getStatus() == VenueStatus.MAINTENANCE) {
                System.out.println(venue);
                found = true;
            }
        }

        if (!found) {
            System.out.println("No under maintenance venues found");
        }
    }

    // display booked venues
    public void displayBookedVenues() {
        boolean found = false;

        for (Venue venue : venueList) {
            if (venue.getStatus() == VenueStatus.BOOKED) {
                System.out.println(venue);
                found = true;
            }
        }

        if (!found) {
            System.out.println("No booked venues found");
        }
    }

    // count total venues
    public int getTotalVenues() {
        return venueList.size();
    }

    // count by status
    public int countVenuesByStatus(VenueStatus status) {
        int count = 0;

        for (Venue venue : venueList) {
            if (venue.getStatus() == status) {
                count++;
            }
        }

        return count;
    }
}
