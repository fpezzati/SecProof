package edu.pezzati.sec;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("/login")
public class Login {

    @POST
    public Response login(String username, String password) {
	Response resp = null;
	return resp;
    }
}
