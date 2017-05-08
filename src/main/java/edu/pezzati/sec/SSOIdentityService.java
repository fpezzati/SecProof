package edu.pezzati.sec;

import java.util.Arrays;
import java.util.Base64;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.apache.ApacheHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;

import edu.pezzati.sec.model.Token;
import edu.pezzati.sec.model.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Path("/login")
public class SSOIdentityService {

    private Logger log = Logger.getLogger(getClass());
    private String jwtSecret = new String(Base64.getEncoder().encode(System.getProperty("jwt.shared.secret").getBytes()));
    private String clientId = System.getProperty("google.client.id");

    @Path("/token")
    @POST
    public void googleLogin(@Context HttpServletRequest request, String token) {
	log.info("Got this token: " + token);
    }

    @Path("/google")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response getGoogleToken(@Context HttpServletResponse resp, String token) throws Exception {
	log.info("Got this token: " + token);
	Token jwtToken = getJwtToken(getUser(getGoogleToken(token)));
	return Response.ok().entity(jwtToken).build();
    }

    private Token getJwtToken(User user) {
	return new Token(Jwts.builder().setSubject(user.getUsername()).signWith(SignatureAlgorithm.HS512, jwtSecret).compact());
    }

    private User getUser(GoogleIdToken googleToken) {
	User user = new User();
	user.setUsername((String) googleToken.getPayload().get("name"));
	return user;
    }

    private GoogleIdToken getGoogleToken(String token) throws Exception {
	HttpTransport transport = new ApacheHttpTransport();
	JsonFactory jsonFactory = new JacksonFactory();
	GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(transport, jsonFactory).setAudience(Arrays.asList(clientId))
		.build();
	return verifier.verify(token);
    }
}