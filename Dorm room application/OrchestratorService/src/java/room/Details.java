/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package room;

public class Details {
    private boolean furnished;
    private String[] amenities;
    private boolean live_in_landlord;
    private long shared_with;
    private boolean bills_included;
    private boolean bathroom_shared;

    public boolean isFurnished() { return furnished; }
    public void setFurnished(boolean furnished) { this.furnished = furnished; }

    public String[] getAmenities() { return amenities; }
    public void setAmenities(String[] amenities) { this.amenities = amenities; }

    public boolean isLiveInLandlord() { return live_in_landlord; }
    public void setLiveInLandlord(boolean live_in_landlord) { this.live_in_landlord = live_in_landlord; }

    public long getSharedWith() { return shared_with; }
    public void setSharedWith(long shared_with) { this.shared_with = shared_with; }

    public boolean isBillsIncluded() { return bills_included; }
    public void setBillsIncluded(boolean bills_included) { this.bills_included = bills_included; }

    public boolean isBathroomShared() { return bathroom_shared; }
    public void setBathroomShared(boolean bathroom_shared) { this.bathroom_shared = bathroom_shared; }
}

