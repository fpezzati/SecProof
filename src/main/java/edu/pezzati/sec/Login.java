package edu.pezzati.sec;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/login")
public class Login {

    private Logger log = LoggerFactory.getLogger(getClass());

    @POST
    public Response login(@FormParam("usrname") String username, @FormParam("passwd") String password) {
	log.info("user {} asks for login.", username);
	return Response.ok("you login succesfully").build();
    }
}
