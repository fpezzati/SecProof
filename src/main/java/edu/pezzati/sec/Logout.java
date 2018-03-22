package edu.pezzati.sec;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/logout")
public class Logout {

    private Logger log = LoggerFactory.getLogger(getClass());

    @GET
    public Response logout() {
	log.info("user {} asks for logout");
	return Response.ok("you logout succesfully").build();
    }
}
