package edu.pezzati.sec;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/logout")
public class Logout {

    private Logger log = LoggerFactory.getLogger(getClass());

    @GET
    public Response logout() {
	Subject subject = SecurityUtils.getSubject();
	log.info("user {} asks for logout", subject.getPrincipal());
	subject.logout();
	return Response.ok("you logout succesfully").build();
    }
}
