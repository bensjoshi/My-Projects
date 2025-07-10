/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package roomclient;

/**
 *
 * @author bensj
 */    

public class Application {
    private String roomID;
    private String name;
    private String startDate;  // Application start date
    private String endDate;    // Application end date   
    private String status;
    private String applicationID;

    // Constructor to initialize the Application object
    public Application(String roomID, String name, String startDate, String endDate) {
        this.roomID = roomID;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    // Getters and Setters
    public String getRoomID() {
        return roomID;
    }

    public void setRoomID(String roomID) {
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
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    
    public String getApplicationID() {
        return applicationID;
    }

    public void setApplicationID(String applicationID) {
        this.applicationID = applicationID;
    }


}
