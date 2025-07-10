package room;


import java.time.LocalDate;
public class RoomResponse {
    private Room[] rooms;

    
        // Default constructor
    public RoomResponse() {}

    // Constructor that accepts a Room[] array
    public RoomResponse(Room[] rooms) {
        this.rooms = rooms;
    }
    public Room[] getRooms() { return rooms; }
    public void setRooms(Room[] value) { this.rooms = value; }
}

