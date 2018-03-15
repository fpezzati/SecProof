package edu.pezzati.sec;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/login")
public class Login {

    private Logger log = LoggerFactory.getLogger(getClass());

    @POST
    public Response login(@FormParam("usrname") String username, @FormParam("passwd") String password) {
	SecurityUtils.getSubject().login(new UsernamePasswordToken(username, password));
	SecurityUtils.getSubject().getPrincipals().asList().stream().forEach(principal -> log.info(principal.toString()));
	return Response.ok("you login succesfully").build();
    }
}
