package edu.pezzati.sec.token;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.web.filter.AccessControlFilter;

public class JWTFilter extends AccessControlFilter {

    private JwtTokenProvider jwtTokenProvider;

    public JWTFilter() {
	jwtTokenProvider = new JwtTokenProvider();
    }

    @Override
    protected boolean isAccessAllowed(ServletRequest req, ServletResponse res, Object arg2) throws Exception {
	String token = ((HttpServletRequest) req).getHeader("Authorization");
	if (token == null || token.isEmpty() || !token.startsWith("Bearer ")) {
	    throw new AuthenticationException("Provided token is not a bearer one.");
	}
	token = token.replace("Bearer ", "");
	jwtTokenProvider.getUser(token);
	return false;
    }

    @Override
    protected boolean onAccessDenied(ServletRequest req, ServletResponse res) throws Exception {
	((HttpServletResponse) res).setStatus(HttpServletResponse.SC_FORBIDDEN);
	return false;
    }

}
