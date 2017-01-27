package edu.pezzati.sec;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/boundary")
public class WebBoundary {

    @GET
    @Path("/resourceA")
    @CanAccess
    public String getResourceA() {
	return "A";
    }

    @GET
    @Path("/resourceB")
    public String getResourceB() {
	return "B";
    }
}
