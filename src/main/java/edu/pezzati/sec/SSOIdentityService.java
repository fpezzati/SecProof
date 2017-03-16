package edu.pezzati.sec;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;

import org.apache.log4j.Logger;

@Path("/login")
public class SSOIdentityService {

    private Logger log = Logger.getLogger(getClass());

    @Path("/google")
    @POST
    public void googleLogin(@Context HttpServletRequest request, String token) {
	log.info("Got this token: " + token);
    }
}
