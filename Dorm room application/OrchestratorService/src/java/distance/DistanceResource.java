package distance;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

@Path("distance")
public class DistanceResource {

    // The base URL of the ORSM API
    private static final String ORSM_API_URL = "http://router.project-osrm.org/route/v1/car/%s?geometries=geojson&overview=full";

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String calculateDistance(@QueryParam("lat1") double lat1,
                                    @QueryParam("lon1") double lon1,
                                    @QueryParam("lat2") double lat2,
                                    @QueryParam("lon2") double lon2) {
        
        // Construct the API request URL with query parameters
        String coordinates = lon1 + "," + lat1 + ";" + lon2 + "," + lat2;
        String apiUrl = String.format(ORSM_API_URL, coordinates);

        // Initialize response string
        StringBuilder response = new StringBuilder();

        try {
            // Create URL object for the OSRM API
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            // Read the response from the OSRM API
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // Parse the response JSON
            Gson gson = new Gson();
            JsonObject jsonResponse = gson.fromJson(response.toString(), JsonObject.class);

            // Get the distance from the API response
            JsonObject route = jsonResponse.getAsJsonArray("routes").get(0).getAsJsonObject();
            double distance = route.get("distance").getAsDouble();

            // Return the distance as a JSON response
            JsonObject result = new JsonObject();
            result.addProperty("distance", distance);  // Distance in meters
            return gson.toJson(result);

        } catch (Exception e) {
            e.printStackTrace();  // Log the error (or use a logger)
            return "{\"error\": \"Unable to calculate distance. Please check the input values.\"}";
        }
    }
}


