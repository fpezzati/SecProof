package edu.pezzati.sec.token;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Provider
@Secured
@Priority(Priorities.AUTHENTICATION)
public class JwtContainerRequestFilter implements ContainerRequestFilter {

    private JwtTokenProvider jwtT;
    private Logger log = LoggerFactory.getLogger(getClass());

    @Context
    ResourceInfo resource;

    public JwtContainerRequestFilter() {
	jwtT = new JwtTokenProvider();
    }

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
	String token = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION).replace("Bearer ", "");
	try {
	    String user = jwtT.getUser(token);
	    String[] permissions = jwtT.getPermissions(token);
	    if (!checkPermissions(permissions))
		requestContext.abortWith(Response.status(Response.Status.FORBIDDEN).build());
	} catch (Exception e) {
	    log.error("Error while parsing token {}", token, e);
	    requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
	}
    }

    private boolean checkPermissions(String[] permissions) {
	Set<String> mandatoryPerms = new HashSet<>();
	Class<?> resourceClass = resource.getResourceClass();
	Secured classAnnot = resourceClass.getAnnotation(Secured.class);
	if (classAnnot != null) {
	    mandatoryPerms.addAll(Arrays.asList(classAnnot.value()));
	}
	Method resourceMethod = resource.getResourceMethod();
	Secured methodAnnot = resourceMethod.getAnnotation(Secured.class);
	if (methodAnnot != null) {
	    mandatoryPerms.addAll(Arrays.asList(methodAnnot.value()));
	}
	boolean allowed = false;
	for (String permission : permissions) {
	    allowed = allowed || mandatoryPerms.contains(permission);
	}
	return allowed;
    }
}
