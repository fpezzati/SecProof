package edu.pezzati.sec;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;

import edu.pezzati.sec.token.JwtTokenProvider;
import edu.pezzati.sec.token.TokenBlacklist;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;

@CanAccess
@Interceptor
public class CanAccessImpl {

    private Logger log = Logger.getLogger(getClass());
    @Inject
    private HttpServletRequest httpServletRequest;
    @Inject
    private JwtTokenProvider jwtTokenProvider;
    @Inject
    private TokenBlacklist blackList;

    @AroundInvoke
    public Object intercept(InvocationContext context) throws Exception {
	log.debug(context.getMethod().getName() + " invoked.");
	String token = httpServletRequest.getHeader("token");
	try {
	    jwtTokenProvider.verifyToken(token);
	    if (blackList.isInBlacklist(jwtTokenProvider.getJwtToken(token)))
		throw new JwtException("Token is invalid.");
	    return context.proceed();
	} catch (ExpiredJwtException expired) {
	    return Response.status(401).entity("Token has expired. Please login again.").build();
	} catch (JwtException invalid) {
	    return Response.status(403).entity("Token is invalid. Please login again.").build();
	} catch (Exception generic) {
	    return Response.status(500).entity("Application error. Please login again.").build();
	}
    }
}
