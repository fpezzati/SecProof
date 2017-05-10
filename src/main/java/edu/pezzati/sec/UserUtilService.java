package edu.pezzati.sec;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import edu.pezzati.sec.token.JwtTokenProvider;
import edu.pezzati.sec.token.TokenBlacklist;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.SignatureException;

@Path("/user")
public class UserUtilService {

    @Inject
    private TokenBlacklist blacklist;
    @Inject
    private JwtTokenProvider tokenProvider;

    @Path("/logout")
    @GET
    public Response logout(@Context HttpServletRequest req) throws SignatureException, JwtException, Exception {
	blacklistToken(req.getHeader("token"));
	return Response.status(200).build();
    }

    @Path("/reset")
    @GET
    public Response resetPassword(@Context HttpServletRequest req) throws SignatureException, JwtException, Exception {
	blacklistToken(req.getHeader("token"));
	return Response.status(200).build();
    }

    private void blacklistToken(String token) throws SignatureException, JwtException, Exception {
	if (token != null && !token.isEmpty())
	    blacklist.put(tokenProvider.getJwtToken(token));
    }
}
