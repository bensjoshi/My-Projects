package roomclient;

import java.util.List;

public class Room {
    
    
    
    private int id;
    private String name;
    private Location location;
    private Details details;
    private double price_per_month_gbp;
    private String availability_date;
    private List<String> spoken_languages;

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Details getDetails() {
        return details;
    }

    public void setDetails(Details details) {
        this.details = details;
    }

    public double getPrice_per_month_gbp() {
        return price_per_month_gbp;
    }

    public void setPrice_per_month_gbp(double price_per_month_gbp) {
        this.price_per_month_gbp = price_per_month_gbp;
    }

    public String getAvailability_date() {
        return availability_date;
    }

    public void setAvailability_date(String availability_date) {
        this.availability_date = availability_date;
    }

    public List<String> getSpoken_languages() {
        return spoken_languages;
    }

    public void setSpoken_languages(List<String> spoken_languages) {
        this.spoken_languages = spoken_languages;
    }

    @Override
    public String toString() {
        return "Room{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", location=" + location +
                ", details=" + details +
                ", price_per_month_gbp=" + price_per_month_gbp +
                ", availability_date='" + availability_date + '\'' +
                ", spoken_languages=" + spoken_languages +
                '}';
    }
}

class Location {
    private String city;
    private String county;
    private String postcode;

    // Getters and Setters
    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    @Override
    public String toString() {
        return "Location{" +
                "city='" + city + '\'' +
                ", county='" + county + '\'' +
                ", postcode='" + postcode + '\'' +
                '}';
    }
}

class Details {
    private boolean furnished;
    private List<String> amenities;
    private boolean live_in_landlord;
    private int shared_with;
    private boolean bills_included;
    private boolean bathroom_shared;

    // Getters and Setters
    public boolean isFurnished() {
        return furnished;
    }

    public void setFurnished(boolean furnished) {
        this.furnished = furnished;
    }

    public List<String> getAmenities() {
        return amenities;
    }

    public void setAmenities(List<String> amenities) {
        this.amenities = amenities;
    }

    public boolean isLive_in_landlord() {
        return live_in_landlord;
    }

    public void setLive_in_landlord(boolean live_in_landlord) {
        this.live_in_landlord = live_in_landlord;
    }

    public int getShared_with() {
        return shared_with;
    }

    public void setShared_with(int shared_with) {
        this.shared_with = shared_with;
    }

    public boolean isBills_included() {
        return bills_included;
    }

    public void setBills_included(boolean bills_included) {
        this.bills_included = bills_included;
    }

    public boolean isBathroom_shared() {
        return bathroom_shared;
    }

    public void setBathroom_shared(boolean bathroom_shared) {
        this.bathroom_shared = bathroom_shared;
    }

    @Override
    public String toString() {
        return "Details{" +
                "furnished=" + furnished +
                ", amenities=" + amenities +
                ", live_in_landlord=" + live_in_landlord +
                ", shared_with=" + shared_with +
                ", bills_included=" + bills_included +
                ", bathroom_shared=" + bathroom_shared +
                '}';
    }
}
