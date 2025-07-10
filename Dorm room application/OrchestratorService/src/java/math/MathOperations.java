/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/WebServices/GenericResource.java to edit this template
 */
package math;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

/**
 * REST Web Service
 *
 * @author bensj
 */
@Path("math")
public class MathOperations {

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of MathOperations
     */
    public MathOperations() {
    }

    /**
     * Retrieves representation of an instance of math.MathOperations
     * @return an instance of java.lang.String
     */
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getJson(@QueryParam("x")int x,@QueryParam("y")int y) {
        
        String jsonString = "{ \"X\": " + x + ", \"Y\": " + y + ", \"Result\": " + (x + y) + " }";
        return jsonString;  
        
    }
    /**
     * PUT method for updating or creating an instance of MathOperations
     * @param content representation for the resource
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public void putJson(String content) {
    }
}
