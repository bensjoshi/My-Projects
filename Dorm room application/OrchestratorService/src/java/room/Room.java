/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package room;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author bensj
 */
    
public class Room {


    private long id;
    private String name;
    private Location location;
    private Details details;
    private long price_per_month_gbp;
    private String availability_date;
    private String[] spoken_languages;
    private String availabilityStatus;

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Location getLocation() { return location; }
    public void setLocation(Location location) { this.location = location; }

    public Details getDetails() { return details; }
    public void setDetails(Details details) { this.details = details; }

    public long getPricePerMonthGbp() { return price_per_month_gbp; }
    public void setPricePerMonthGbp(long price_per_month_gbp) { this.price_per_month_gbp = price_per_month_gbp; }

    public String getAvailabilityDate() { return availability_date; }
    public void setAvailabilityDate(String availability_date) { this.availability_date = availability_date; }

    public String[] getSpokenLanguages() { return spoken_languages; }
    public void setSpokenLanguages(String[] spoken_languages) { this.spoken_languages = spoken_languages; }
    
    public boolean isAvailable() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate availability = LocalDate.parse(availability_date, formatter);
        LocalDate currentDate = LocalDate.now();

        // The room is available if the availability date is in the past or today
        return !availability.isAfter(currentDate);
    }
    
    public String getAvailabilityStatus() {
        return availabilityStatus;
    }
    
    public void setAvailabilityStatus(String availabilityStatus) {
        this.availabilityStatus = availabilityStatus;
    }

}

