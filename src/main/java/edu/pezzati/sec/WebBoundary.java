package edu.pezzati.sec;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import edu.pezzati.sec.controller.InnerResource;

@Path("/boundary")
public class WebBoundary {

    @Inject
    private InnerResource resource;

    @GET
    @Path("/resourceA")
    @CanAccess
    public Response getResourceA() {
	return Response.status(200).entity("A").build();
    }

    @GET
    @Path("/resourceB")
    public Response getResourceB() {
	return Response.status(200).entity("B").build();
    }

    @GET
    @Path("/resourceC")
    public String getResourceC() {
	return resource.getResourceName();
    }
}
