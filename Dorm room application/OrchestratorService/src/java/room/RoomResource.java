package room;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("room")
public class RoomResource {

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of RoomResource
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getRooms() {
        Room[] rooms = readRoomsFromJson();

        if (rooms != null) {
            // Convert the array of rooms into a list and add availability info
            for (Room room : rooms) {
                // Add the availability status as a new field in each room object
                room.setAvailabilityStatus(room.isAvailable() ? "Available" : "Not Available");
            }

            Gson gson = new Gson();
            return gson.toJson(rooms);  // Return all rooms with availability status
        } else {
            return "{\"error\": \"Error reading JSON data\"}";
        }
    }

    
    @GET
    @Path("applications")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getApplications() {
        // File path to the applications.json file
        File file = new File("C:/Users/bensj/Documents/NetBeansProjects/OrchestratorService/src/resources/applications.json");

        // Read the existing applications from the file
        List<Application> applications = readApplicationsFromFile(file);

        if (applications == null) {
            return Response.status(500).entity("{\"error\": \"Error reading applications file\"}").build();
        }

        // Use Gson to convert the list of applications to JSON string
        Gson gson = new Gson();
        String jsonResponse = gson.toJson(applications);

        return Response.status(200).entity(jsonResponse).build();
    }

    


    
        /**
     * Search for rooms based on various filters like price, location, amenities, etc.
     */
    @GET
    @Path("search")
    @Produces(MediaType.APPLICATION_JSON)
    public String searchRooms(@QueryParam("city") String city)
{
        Room[] rooms = readRoomsFromJson();

        if (rooms != null) {
            List<Room> filteredRooms = new ArrayList<>();

            for (Room room : rooms) {
                boolean matches = true;

                // Filter by city
                if (city != null && !city.isEmpty() && !room.getLocation().getCity().equalsIgnoreCase(city)) {
                    matches = false;
                }



                // If it matches all criteria, add to the filtered list
                if (matches) {
                    filteredRooms.add(room);
                }
            }

            Gson gson = new Gson();
            return gson.toJson(filteredRooms);
        } else {
            return "{\"error\": \"Error reading JSON data\"}";
        }
    }
  
    
    @POST
    @Path("apply")
    @Consumes(MediaType.APPLICATION_JSON)
        public Response applyForRoom(String jsonInput) {
            // Print the received JSON input
            System.out.println("Received room application data:");
            System.out.println(jsonInput);

            // Use Gson to deserialize the JSON input into a RoomApplication object
            Gson gson = new Gson();
            Application roomApplication = gson.fromJson(jsonInput, Application.class);

            
                // Automatically generate a unique applicationID
            List<Application> existingApplications = readApplicationsFromFile(new File("C:/Users/bensj/Documents/NetBeansProjects/OrchestratorService/src/resources/applications.json"));
            int applicationID = generateApplicationID(existingApplications);
            roomApplication.setApplicationID(applicationID);

            // Print the deserialized object (optional, for debugging)
            System.out.println("Application:");
            System.out.println("Room ID: " + roomApplication.getRoomID());
            System.out.println("Name: " + roomApplication.getName());
            System.out.println("Start Date: " + roomApplication.getStartDate());
            System.out.println("End Date: " + roomApplication.getEndDate());
            System.out.println("Application Status: " + roomApplication.getStatus());
            System.out.println("Generated Application ID: " + roomApplication.getApplicationID());
                    
            try {
                writeApplicationToFile(roomApplication);
            } catch (IOException e) {
                // Handle file writing error
                return Response.status(500).entity("Error writing to file: " + e.getMessage()).build();
            }

            // Send a response back to the client
            String responseMessage = "Room application data received successfully.";
            return Response.status(200).entity(responseMessage).build();
        }


    private Room getRoomById(int roomId, Room[] rooms) {
        for (Room room : rooms) {
            if (room.getId() == roomId) {
                return room;
            }
        }
        return null;
    }
    
    private void writeApplicationToFile(Application application) throws IOException {
        // Create or open the file for writing
        File file = new File("C:/Users/bensj/Documents/NetBeansProjects/OrchestratorService/src/resources/applications.json");
        boolean fileExists = file.exists();

        // Prepare Gson for serialization
        Gson gson = new Gson();

        // Create a BufferedWriter to append to the file
      try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            // If the file is empty, add an opening bracket for the JSON array
            if (!fileExists || file.length() == 0) {
                writer.write("[\n"); // Opening the JSON array
            } else {
                writer.write(",\n"); // Add a comma to separate objects (if not the first entry)
            }

            // Serialize the application object to JSON and write it to the file
            String json = gson.toJson(application);
            writer.write(json);

            // No need to write closing bracket here yet, we will do that later
        }
    }



    private List<Application> readApplicationsFromFile(File file) {

        // StringBuilder to store the file content
        StringBuilder fileContent = new StringBuilder();
        
        // Read the content of the file
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                fileContent.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        // Check if the file contains valid JSON array data
        String jsonResponse = fileContent.toString().trim();

        if (jsonResponse.isEmpty()) {
            return new ArrayList<>();
        }

        // If file ends with a closing bracket without proper commas
        if (jsonResponse.endsWith("}")) {
            jsonResponse = jsonResponse + "]";
        }

        // Add closing bracket if it's missing and the file isn't empty
        if (!jsonResponse.endsWith("]")) {
            jsonResponse += "]";
        }

        
        // Use Gson to parse the JSON array and ensure it is valid
        Gson gson = new Gson();
        try {
            // Parse the JSON string into a List of Application objects
            return gson.fromJson(jsonResponse, new TypeToken<List<Application>>() {}.getType());
        } catch(JsonSyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }


    // Method to read and parse the rooms from a JSON file
    private Room[] readRoomsFromJson() {
        try (FileReader reader = new FileReader("C:/Users/bensj/Documents/NetBeansProjects/OrchestratorService/src/resources/rooms.json")) {
            // Create Gson instance to parse JSON data
            Gson gson = new Gson();
           
            RoomResponse roomResponse = gson.fromJson(reader, RoomResponse.class);
            return roomResponse.getRooms();
            
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    

private int generateApplicationID(List<Application> existingApplications) {
    // Simple method to generate a new unique applicationID
    // If there are existing applications, find the highest existing ID and add 1
    int maxID = 0;
    if (existingApplications != null && !existingApplications.isEmpty()) {
        for (Application app : existingApplications) {
            if (app.getApplicationID() > maxID) {
                maxID = app.getApplicationID();
            }
        }
    }
    return maxID + 1;
}
    
    @DELETE
    @Path("cancel/{applicationID}")
    public Response cancelApplication(@PathParam("applicationID") int applicationID) throws IOException {
        // File path to the applications.json file
        File file = new File("C:/Users/bensj/Documents/NetBeansProjects/OrchestratorService/src/resources/applications.json");

        // Read the existing applications from the file
        List<Application> applications = readApplicationsFromFile(file);

        if (applications == null) {
            return Response.status(500).entity("{\"error\": \"Error reading applications file\"}").build();
        }

        // Find and remove the application with the specified ID
        Application applicationToCancel = null;
        for (Application app : applications) {
            if (app.getApplicationID() == applicationID) {
                applicationToCancel = app;
                break;
            }
        }

        if (applicationToCancel == null) {
            return Response.status(404).entity("{\"error\": \"Application not found\"}").build();
        }

        // Mark application as canceled (you could also remove it if preferred)
        applicationToCancel.setStatus("Cancelled");

        // Write the updated list of applications back to the file
         Gson gson = new Gson();
        
        // Open the file for writing
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            // Write the updated list as a JSON array
            String json = gson.toJson(applications);
            writer.write(json);
       
        } catch (IOException e) {
            return Response.status(500).entity("{\"error\": \"Error writing to applications file\"}").build();
        }

        // Return a success message
        return Response.status(200).entity("{\"message\": \"Application cancelled successfully\"}").build();
    }


}