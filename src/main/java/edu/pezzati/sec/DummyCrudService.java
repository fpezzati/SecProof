package edu.pezzati.sec;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("/dummy")
public class DummyCrudService {

    @POST
    public Response create() {
	return Response.ok().entity("you do a @POST.").build();
    }

    @GET
    public Response read() {
	return Response.ok().entity("you do a @GET.").build();
    }

    @PUT
    public Response update() {
	return Response.ok().entity("you do a @PUT.").build();
    }

    @DELETE
    public Response delete() {
	return Response.ok().entity("you do a @DELETE.").build();
    }
}
