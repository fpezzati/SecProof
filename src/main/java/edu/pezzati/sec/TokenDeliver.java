package edu.pezzati.sec;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import edu.pezzati.sec.model.Token;
import edu.pezzati.sec.model.User;
import edu.pezzati.sec.token.JwtTokenProvider;

@Path("/token")
public class TokenDeliver {

    private JwtTokenProvider jwtTokenProvider;
    private HashMap<String, String[]> roles;
    private HashMap<String, String[]> permissions;

    public TokenDeliver() {
	jwtTokenProvider = new JwtTokenProvider();
	roles = new HashMap<>();
	roles.put("user1", new String[] { "admin" });
	roles.put("user2", new String[] { "dataprovider" });
	roles.put("user3", new String[] { "mantainer" });
	roles.put("user4", new String[] { "jrmantainer" });
	permissions = new HashMap<>();
	permissions.put("admin", new String[] { "*" });
	permissions.put("dataprovider", new String[] { "resource:read" });
	permissions.put("mantainer", new String[] { "resource:*" });
	permissions.put("jrmantainer", new String[] { "resource:read", "resource:update" });
    }

    @GET
    public Response getToken(@QueryParam("username") String username) {
	User user = new User();
	user.setUsername(username);
	user.setPermissions(computePermissions(username));
	Token token = jwtTokenProvider.getJwtToken(user);
	return Response.ok().entity(token.getJwtToken()).build();
    }

    private String[] computePermissions(String username) {
	ArrayList<String> userPermissions = new ArrayList<>();
	String[] userRoles = roles.get(username);
	for (String userRole : userRoles) {
	    userPermissions.addAll(Arrays.asList(permissions.get(userRole)));
	}
	return userPermissions.toArray(new String[userPermissions.size()]);
    }
}
