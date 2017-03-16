package edu.pezzati.sec;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;

@CanAccess
@Interceptor
public class CanAccessImpl {

    private Logger log = Logger.getLogger(getClass());
    @Inject
    private HttpServletRequest httpServletRequest;

    @AroundInvoke
    public Object intercept(InvocationContext context) throws Exception {
	log.debug(context.getMethod().getName() + " invoked.");
	String token = httpServletRequest.getHeader("token");
	verifyToken(token);
	return context.proceed();
    }

    private void verifyToken(String token) throws Exception {
	GoogleIdTokenVerifier tokenVerifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory()).build();
	GoogleIdToken validToken = tokenVerifier.verify(token);
	String username = (String) validToken.getPayload().get("name");
	log.debug("User " + username + " landed on.");
    }
}
