package roomclient;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RoomClient {

    private static final String BASE_URL = "http://localhost:8080/OrchestratorService/webresources";
    private static final String ROOM_ENDPOINT = "/room";
    private static final String WEATHER_ENDPOINT = "/weather";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("----------------------------");
            System.out.println("Room Application System");
            System.out.println("1. Search Rooms");
            System.out.println("2. Apply for Room");
            System.out.println("3. View Room Details");
            System.out.println("4. View Location Weather");
            System.out.println("5. View Distance from campus");
            System.out.println("6. View Applications History");
            System.out.println("7. Cancel an application");
            System.out.println("8. Display city coordinates (For location)");
            System.out.println("9. Exit application");
            System.out.println("----------------------------");
            System.out.print("Choose an option: ");
            
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline
            
            switch (choice) {
                case 1:
                    searchRooms(scanner);
                    break;
                case 2:
                    applyForRoom(scanner);
                    break;
                case 3:
                    viewRoomDetails(scanner);
                    break;
                case 4:
                    viewLocationWeather(scanner);
                    break;
                case 5:
                    viewDistance(scanner);
                    break;
                case 6:
                    viewApplications(scanner);
                    break;
                case 7:
                    cancelApplication(scanner);
                    break;
                case 8:
                    displayCityCoordinates(scanner);
                    break;
                case 9:
                    System.out.println("Exiting...");
                    return;
                default:
                    System.out.println("Invalid option. Try again.");
            }
        }
    }
    
    //This is the function for viewing the weather json data from the rest api
    public static void viewLocationWeather(Scanner scanner) {
        System.out.print("Enter the latitude: ");
        double latitude = scanner.nextDouble();

        System.out.print("Enter the longitude: ");
        double longitude = scanner.nextDouble();
        scanner.nextLine(); // Consume newline

        try {
            // Construct the URL with the query parameters
            String apiUrl = BASE_URL + WEATHER_ENDPOINT + "?lon=" + longitude + "&lat=" + latitude 
                            + "&lang=en&unit=metric&output=json";

            // Fetch the JSON response from the API
            String jsonResponse = fetchJson(apiUrl);

            if (jsonResponse != null) {
                // Parse the JSON response to extract weather details
                Gson gson = new Gson();
                JsonObject weatherResponse = gson.fromJson(jsonResponse, JsonObject.class);

                // Parse the "dataseries" array which contains weather data for multiple days
                JsonArray dataSeries = weatherResponse.getAsJsonArray("dataseries");

                if (dataSeries != null && dataSeries.size() > 0) {
                    System.out.println("\nWeather Forecast:");

                    // Loop through each day's forecast and print the weather data
                    for (int i = 0; i < dataSeries.size(); i++) {
                        JsonObject dayData = dataSeries.get(i).getAsJsonObject();
                        int date = dayData.get("date").getAsInt();  // The date in YYYYMMDD format
                        String weatherCondition = dayData.get("weather").getAsString();
                        JsonObject temp2m = dayData.getAsJsonObject("temp2m");
                        double maxTemp = temp2m.get("max").getAsDouble();
                        double minTemp = temp2m.get("min").getAsDouble();
                        int windMax = dayData.get("wind10m_max").getAsInt();

                        // Display the weather data for each day
                        System.out.println("Date: " + formatDate(date));
                        System.out.println("Weather Condition: " + weatherCondition);
                        System.out.println("Max Temperature: " + maxTemp + "°C");
                        System.out.println("Min Temperature: " + minTemp + "°C");
                        System.out.println("Max Wind Speed: " + windMax + " m/s");
                        System.out.println("----------------------------");
                          } 
                }else {
                    System.out.println("Could not retrieve weather details for the given coordinates.");
                }
            } else {
                System.out.println("Error fetching weather data.");
            }
        } catch (Exception e) {
            System.err.println("Error while fetching weather: " + e.getMessage());
        }
    }
    
    public static void viewDistance(Scanner scanner){
        System.out.print("Enter the latitude: ");
        double lat1 = scanner.nextDouble();

        System.out.print("Enter the longitude: ");
        double lon1 = scanner.nextDouble();
        scanner.nextLine(); // Consume newline
        
        System.out.print("Enter the latitude: ");
        double lat2 = scanner.nextDouble();

        System.out.print("Enter the longitude: ");
        double lon2 = scanner.nextDouble();
        scanner.nextLine(); // Consume newline
        
        
        try {
            // Construct the URL with the query parameters
            String apiUrl = BASE_URL + "/distance?lat1=" + lat1 + "&lon1=" + lon1 + "&lat2=" + lat2 + "&lon2=" + lon2;

            
            // Make the HTTP GET request and fetch the response
            String jsonResponse = fetchJson(apiUrl);
            
            if (jsonResponse != null) {
                // Assuming the response contains the distance in a field called "distance"
                System.out.println("\nDistance between the locations: " + jsonResponse);
            } else {
                System.out.println("Error fetching distance data.");
            }
}
        catch (Exception e) {
            System.err.println("Error while fetching distance: " + e.getMessage());
        }
    }

        //This formats the date to make it easier to read
    private static String formatDate(int date) {
             String dateStr = String.valueOf(date);
             // Extract year, month, and day from the date string
             String year = dateStr.substring(0, 4);
             String month = dateStr.substring(4, 6);
             String day = dateStr.substring(6);
             return day + "/" + month + "/" + year;
         }

    //function to apply for a room
    public static void applyForRoom(Scanner scanner) {

            System.out.print("Enter Room ID to apply for: ");
            int roomId = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            System.out.print("Enter your name: ");
            String name = scanner.nextLine();

            // Prompt for start date
            System.out.print("Enter start date (YYYY-MM-DD): ");
            String startDate = scanner.nextLine();

            // Prompt for end date
            System.out.print("Enter end date (YYYY-MM-DD): ");
            String endDate = scanner.nextLine();

            // Create an application object
            Application application = new Application(String.valueOf(roomId), name, startDate, endDate);


            // Serialize the application object to JSON
            Gson gson = new Gson();
            String applicationJson = gson.toJson(application);

            try {
                // Send POST request with the application data
                String apiUrl = BASE_URL + ROOM_ENDPOINT + "/apply";
                String response = sendPostRequest(apiUrl, applicationJson);

                // Print response
                if (response != null) {
                    System.out.println("Application Response: " + response);
                } else {
                    System.out.println("Failed to apply for the room.");
                }
            } catch (Exception e) {
                System.err.println("Error applying for room: " + e.getMessage());
            }
    }
    
    
   
    public static String sendPostRequest(String apiUrl, String jsonData) throws Exception {
        HttpURLConnection connection = null;
        BufferedReader reader = null;
        StringBuilder response = new StringBuilder();

        try {
            // Create URL object from the string
            URL url = new URL(apiUrl);

            // Open connection and set up the request
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            connection.setDoOutput(true);  // Enable output stream for request body

            // Write JSON data to the request body
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonData.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // Get response code
            int responseCode = connection.getResponseCode();

            // Read the response if the request was successful (HTTP 200)
            if (responseCode == HttpURLConnection.HTTP_OK) {
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;

                // Read the response
                while ((inputLine = reader.readLine()) != null) {
                    response.append(inputLine);
                }
                return response.toString(); // Return the full response as a string
            } else {
                System.err.println("Error: " + responseCode);
                return null;
            }
        } finally {
            if (reader != null) {
                reader.close();
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
    }



    
    
    
    
    
    
    public static String fetchJson(String urlString) throws Exception {
        HttpURLConnection connection = null;
        BufferedReader reader = null;
        StringBuilder response = new StringBuilder();

        try {
            // Create URL object from the string
            URL url = new URL(urlString);

            // Open connection and set up the request
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json"); // Expect JSON response

            // Get response code
            int responseCode = connection.getResponseCode();

            // Check if the response is successful (HTTP 200)
            if (responseCode == HttpURLConnection.HTTP_OK) {
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;

                // Read the response
                while ((inputLine = reader.readLine()) != null) {
                    response.append(inputLine);
                }
                return response.toString(); // Return the full response as a string
            } else {
                System.err.println("Error: " + responseCode);
                return null;
            }
        } finally {
            if (reader != null) {
                reader.close();
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    /**
     * Process the JSON response from the server.
     *
     * @param jsonResponse The JSON response as a string.
     * @return List of Room objects.
     */
    public static List<Room> processRoomsData(String jsonResponse) {
        try {
            // Use Gson to convert the JSON response into a list of Room objects
            Gson gson = new Gson();
            return gson.fromJson(jsonResponse, new TypeToken<List<Room>>() {}.getType());
        } catch (Exception e) {
            System.err.println("Error processing the room data: " + e.getMessage());
            return null;
        }
    }

    /**
     * Finds a room by ID from the list.
     *
     * @param rooms List of rooms to search from.
     * @param roomId ID of the room to search for.
     * @return The room if found, otherwise null.
     */
    public static Room findRoomById(List<Room> rooms, int roomId) {
        for (Room room : rooms) {
            if (room.getId() == roomId) {
                return room;
            }
        }
        return null; // Room not found
    }

    
    public static void cancelApplication(Scanner scanner) {
        System.out.print("Enter the application ID to cancel: ");
        String input = scanner.nextLine();

        try {
            String apiURL = "http://localhost:8080/OrchestratorService/webresources/room/cancel/" + input;

            // Create a URL object from the URL string
            URL url = new URL(apiURL);

            // Open a connection to the URL
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Set the HTTP method to DELETE
            connection.setRequestMethod("DELETE");


            // Connect to the server
            connection.connect();

            // Get the response code from the server
            int responseCode = connection.getResponseCode();

            // Check if the response code indicates success
            if (responseCode == HttpURLConnection.HTTP_OK) {
                System.out.println("Application cancelled successfully.");
            } else {
                // If the application was not found or some error occurred
                System.out.println("Failed to cancel application. Response Code: " + responseCode);
            }

            // Close the connection
            connection.disconnect();

        } catch (Exception e) {
            // Handle any exceptions (e.g., network issues, invalid URL)
            System.out.println("Error occurred while canceling application: " + e.getMessage());
        }
    }

    
    
    public static void searchRooms(Scanner scanner) {
        System.out.print("Enter city to search for rooms: ");
        String city = scanner.nextLine();

        try {
            String apiUrl = BASE_URL + ROOM_ENDPOINT + "/search?city=" + city;
            String jsonResponse = fetchJson(apiUrl);
            
            if (jsonResponse != null) {
                List<Room> rooms = processRoomsData(jsonResponse);

                if (rooms != null && !rooms.isEmpty()) {
                    System.out.println("\nAvailable rooms in " + city + ":");
                    for (Room room : rooms) {
                    System.out.println("\nRoom Details:");
                    System.out.println("ID: " + room.getId());
                    System.out.println("Name: " + room.getName());

                    // Location Details
                    System.out.println("Location:");
                    System.out.println(" - City: " + room.getLocation().getCity());
                    System.out.println(" - County: " + room.getLocation().getCounty());
                    System.out.println(" - Postcode: " + room.getLocation().getPostcode());

                    // Room Details (Price, Availability, etc.)
                    System.out.println("Price (per month): £" + room.getPrice_per_month_gbp());
                    System.out.println("Availability Date: " + room.getAvailability_date());

                    System.out.println("Room Details:");
                    System.out.println(" - Furnished: " + (room.getDetails().isFurnished() ? "Yes" : "No"));
                    System.out.println(" - Live-in Landlord: " + (room.getDetails().isLive_in_landlord() ? "Yes" : "No"));
                    System.out.println(" - Bills Included: " + (room.getDetails().isBills_included() ? "Yes" : "No"));
                    System.out.println(" - Bathroom Shared: " + (room.getDetails().isBathroom_shared() ? "Yes" : "No"));
                    System.out.println(" - Shared with: " + room.getDetails().getShared_with() + " people");

                    // Amenities
                    System.out.println("Amenities:");
                    for (String amenity : room.getDetails().getAmenities()) {
                        System.out.println(" - " + amenity);
                    }

                    // Spoken Languages
                    System.out.println("Spoken Languages:");
                    for (String language : room.getSpoken_languages()) {
                        System.out.println(" - " + language);
                    }
                }

                } else {
                    System.out.println("No rooms found in this city.");
                }
            } else {
                System.out.println("Error fetching room data.");
            }
        } catch (Exception e) {
            System.err.println("Error while searching rooms: " + e.getMessage());
        }
    }
    /**
     * Displays detailed information about a selected room.
     *
     */
    public static void viewRoomDetails(Scanner scanner) {
        try {
            // URL to fetch all room details from the server
                String API_URL = BASE_URL + ROOM_ENDPOINT;

                // Fetch room data from the API
                String jsonResponse = fetchJson(API_URL);

                // Check if the response is valid
                if (jsonResponse != null) {
                    // Process the JSON response into a list of Room objects
                    List<Room> rooms = processRoomsData(jsonResponse);

                    // Prompt user for the room ID they want to view details for
                    System.out.print("Enter the room ID to view details: ");
                    String input = scanner.nextLine();

                    if (!input.isEmpty()) {
                        int roomId = Integer.parseInt(input);
                        Room selectedRoom = findRoomById(rooms, roomId);

                        if (selectedRoom != null) {
                            // Print the details for the selected room
                            System.out.println("\nRoom Details:");
                            System.out.println("ID: " + selectedRoom.getId());
                            System.out.println("Name: " + selectedRoom.getName());

                            // Location Details
                            System.out.println("Location:");
                            System.out.println(" - City: " + selectedRoom.getLocation().getCity());
                            System.out.println(" - County: " + selectedRoom.getLocation().getCounty());
                            System.out.println(" - Postcode: " + selectedRoom.getLocation().getPostcode());

                            // Room Details (Price, Availability, etc.)
                            System.out.println("Price (per month): £" + selectedRoom.getPrice_per_month_gbp());
                            System.out.println("Availability Date: " + selectedRoom.getAvailability_date());

                            // Room Features (Furnished, Shared, etc.)
                            System.out.println("Room Features:");
                            System.out.println(" - Furnished: " + (selectedRoom.getDetails().isFurnished() ? "Yes" : "No"));
                            System.out.println(" - Live-in Landlord: " + (selectedRoom.getDetails().isLive_in_landlord() ? "Yes" : "No"));
                            System.out.println(" - Bills Included: " + (selectedRoom.getDetails().isBills_included() ? "Yes" : "No"));
                            System.out.println(" - Bathroom Shared: " + (selectedRoom.getDetails().isBathroom_shared() ? "Yes" : "No"));
                            System.out.println(" - Shared with: " + selectedRoom.getDetails().getShared_with() + " people");

                            // Amenities
                            System.out.println("Amenities:");
                            for (String amenity : selectedRoom.getDetails().getAmenities()) {
                                System.out.println(" - " + amenity);
                            }

                            // Spoken Languages
                            System.out.println("Spoken Languages:");
                            for (String language : selectedRoom.getSpoken_languages()) {
                                System.out.println(" - " + language);
                            }
                        } else {
                            System.out.println("Room ID " + roomId + " not found.");
                        }
                    } else {
                        System.out.println("No room ID entered.");
                    }
                } else {
                    System.out.println("Failed to retrieve room data from the server.");
                }
            } catch (Exception e) {
                System.err.println("Error while viewing room details: " + e.getMessage());
            }
        }

    private static void viewApplications(Scanner scanner) {
        try {
            String API_URL = BASE_URL + ROOM_ENDPOINT + "/applications";

            // Fetch room data from the API
            String jsonResponse = fetchJson(API_URL);
            // Check if the response is not null
            if (jsonResponse != null) {
            // Deserialize the JSON response to a list of Application objects
            Gson gson = new Gson();
            List<Application> applications = gson.fromJson(jsonResponse, new TypeToken<List<Application>>(){}.getType());

            // Check if there are any applications
            if (applications != null && !applications.isEmpty()) {
                System.out.println("\nApplications History:");

                // Iterate over the applications and display their details
                for (Application application : applications) {
                    System.out.println("\nApplication Details:");
                    System.out.println("Room ID: " + application.getRoomID());
                    System.out.println("Applicant Name: " + application.getName());
                    System.out.println("Start Date: " + application.getStartDate());
                    System.out.println("End Date: " + application.getEndDate());
                    System.out.println("Status: " + application.getStatus());
                    System.out.println("Application ID : " + application.getApplicationID());
                    System.out.println("----------------------------");
                }
            } else {
                System.out.println("No applications found.");
            }
        } else {
            System.out.println("Error fetching application data.");
        }
    } catch (Exception e) {
        System.err.println("Error while fetching application data: " + e.getMessage());
    }
}
    public static void displayCityCoordinates(Scanner scanner) {
        // City names and their corresponding latitude and longitude
        String[] cities = {"Nottingham", "Manchester", "Bristol", "London", "Edinburgh"};
        double[][] coordinates = {
            {52.950001, -1.150000},  // Nottingham
            {53.483959, -2.244644},  // Manchester
            {51.454514, -2.587910},  // Bristol
            {51.509865, -0.118092},  // London
            {55.953251, -3.188267}   // Edinburgh
        };

        // Displaying city names and their coordinates
        System.out.println("City Coordinates:");
        for (int i = 0; i < cities.length; i++) {
            System.out.printf("%-12s Latitude: %f, Longitude: %f%n", cities[i], coordinates[i][0], coordinates[i][1]);
        }
    }
}
