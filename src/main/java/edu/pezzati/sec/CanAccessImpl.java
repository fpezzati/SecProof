package edu.pezzati.sec;

import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

import org.apache.log4j.Logger;

@CanAccess
@Interceptor
public class CanAccessImpl {

    private Logger log = Logger.getLogger(getClass());

    @AroundInvoke
    public Object canAccess(InvocationContext context) throws Exception {
	log.debug(context.getMethod().getName() + " invoked.");
	SecurityManager sManager = System.getSecurityManager();
	Object securityContext = sManager.getSecurityContext();
	log.debug(securityContext.toString());
	return context.proceed();
    }
}
