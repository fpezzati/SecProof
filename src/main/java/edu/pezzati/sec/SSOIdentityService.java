package edu.pezzati.sec;

import java.io.IOException;
import java.util.Arrays;

import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;

import org.apache.log4j.Logger;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;

@Path("/login")
public class SSOIdentityService {

    private Logger log = Logger.getLogger(getClass());
    private String clientId = System.getProperty("google.clientid");
    @Inject
    private ServletContext servletContext;

    @Path("/token")
    @POST
    public void googleLogin(@Context HttpServletRequest request, String token) {
	log.info("Got this token: " + token);
    }

    @Path("/google")
    @GET
    public void getGoogleToken(@Context HttpServletResponse resp, @QueryParam("code") String token) throws IOException {
	log.info("Got this token: " + token);
	resp.sendRedirect(servletContext.getContextPath());
    }

    @Path("/now")
    @GET
    public void loginByGoogle(@Context HttpServletResponse resp) throws IOException {
	String url = new GoogleAuthorizationCodeRequestUrl(clientId, "http://localhost:8080/SecProof/srv/login/google",
		Arrays.asList("https://www.googleapis.com/auth/userinfo.email", "https://www.googleapis.com/auth/userinfo.profile"))
			.build();
	resp.sendRedirect(url);
    }

    /**
     * https://accounts.google.com/o/oauth2/iframerpc?action=issueToken&
     * response_type=token%20id_token&scope=openid%20profile%20email&client_id=
     * 276167503151-u1k7lgvd642u650dm3pflql6qj6ok049.apps.googleusercontent.com&
     * login_hint=AJDLj6JUa8yxXrhHdWRHIV0S13cAcEe-
     * kRicaRTibSzBLBnr483f6JMnjaYNnOurnDsMtyY4PaCjl3gx-zwCSg7EevbSEBvhWQ&
     * ss_domain=http%3A%2F%2Flocalhost%3A8080&origin=http%3A%2F%2Flocalhost%
     * 3A8080
     */
}