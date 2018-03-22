package edu.pezzati.sec;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import edu.pezzati.sec.token.Secured;

@Path("/dummy")
public class DummyCrudService {

    @POST
    @Secured(value = { "resource:create" })
    public Response create() {
	return Response.ok().entity("you do a @POST.").build();
    }

    @GET
    @Secured(value = { "resource:read" })
    public Response read() {
	return Response.ok().entity("you do a @GET.").build();
    }

    @PUT
    @Secured(value = { "resource:update" })
    public Response update() {
	return Response.ok().entity("you do a @PUT.").build();
    }

    @DELETE
    @Secured(value = { "resource:delete" })
    public Response delete() {
	return Response.ok().entity("you do a @DELETE.").build();
    }
}
