package edu.pezzati.sec.token;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.AccessControlFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JWTFilter extends AccessControlFilter {

    private JwtTokenProvider jwtTokenProvider;
    private Logger log;

    public JWTFilter() {
	jwtTokenProvider = new JwtTokenProvider();
	log = LoggerFactory.getLogger(getClass());
    }

    @Override
    protected boolean isAccessAllowed(ServletRequest req, ServletResponse res, Object arg2) throws Exception {
	try {
	    String token = ((HttpServletRequest) req).getHeader("Authorization");
	    log.info("checking {} token.", token);
	    if (token == null || token.isEmpty() || !token.startsWith("Bearer ")) {
		return false;
	    }
	    token = token.replace("Bearer ", "");
	    String user = jwtTokenProvider.getUser(token);
	    String[] permissions = jwtTokenProvider.getPermissions(token);
	    log.info("user {} require {}", user, ((HttpServletRequest) req).getRequestURI());
	    Subject subject = SecurityUtils.getSubject();
	    subject.login(new JWTToken(token, user, permissions));
	    return subject.isAuthenticated();
	} catch (Exception e) {
	    log.error("error while checking token.", e);
	    return false;
	}
    }

    @Override
    protected boolean onAccessDenied(ServletRequest req, ServletResponse res) throws Exception {
	((HttpServletResponse) res).setStatus(HttpServletResponse.SC_FORBIDDEN);
	return false;
    }

}
