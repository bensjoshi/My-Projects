package room;

import java.time.LocalDate;

public class Application {
    private int roomID;
    private String name;
    private String status;        // Application status (e.g., Pending, Approved)
    private String startDate;  // Application start date
    private String endDate;    // Application end date
    private int applicationID;
    

    // Getters and setters
    public int getRoomID() {
        return roomID;
    }

    public void setRoomID(int roomID) {
        this.roomID = roomID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
    
    public Application() {
        this.status = "pending";  // Default status is always "pending"
    }
    
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) { this.status = status; }
    
    public int getApplicationID() {
        return applicationID;
    }

    public void setApplicationID(int applicationID) {
        this.applicationID = applicationID;
    }



}
