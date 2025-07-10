/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/WebServices/GenericResource.java to edit this template
 */

package weather;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Path("weather")
public class WeatherResource {

     // Accept parameters dynamically via query string in the URL
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getJson(
            @QueryParam("lon") String lon,
            @QueryParam("lat") String lat,
            @QueryParam("lang") String lang,
            @QueryParam("unit") String unit,
            @QueryParam("output") String output) {



        // Construct query parameters string
        String queryParams = String.format(
            "lon=%s&lat=%s&lang=%s&unit=%s&output=%s",
            lon, lat, lang, unit, output
        );

        try {
            // Construct the URL
            URL url = new URL("https://www.7timer.info/bin/civillight.php?" + queryParams);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.connect();

            // Read the response
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line).append("\r\n");
            }
            in.close();
            
            System.out.println("Raw API Response: " + response.toString());

            // Use Gson to deserialize JSON
            Gson gson = new Gson();
            WeatherResponse weatherResponse = gson.fromJson(response.toString(), WeatherResponse.class);
            
            

            return gson.toJson(weatherResponse);  // Return the deserialized object

        } catch (IOException | JsonSyntaxException e) {
            e.printStackTrace();
            return null;  // Return null or handle error
        }
    }
}

