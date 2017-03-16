package edu.pezzati.sec;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

import edu.pezzati.sec.controller.InnerResource;

@Path("/boundary")
public class WebBoundary {

    @Inject
    private InnerResource resource;

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

    @GET
    @Path("/resourceC")
    public String getResourceC() {
	return resource.getResourceName();
    }
}
