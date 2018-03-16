package edu.pezzati.sec.token;

import java.util.Arrays;
import java.util.HashSet;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JWTAuthorizingRealm extends AuthorizingRealm {

    public static final String REALMNAME = "jwtrealm";
    private JwtTokenProvider jwtProv;
    private Logger log;

    public JWTAuthorizingRealm() {
	jwtProv = new JwtTokenProvider();
	log = LoggerFactory.getLogger(getClass());
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
	SimpleAuthorizationInfo authorizationInfo;
	try {
	    authorizationInfo = new SimpleAuthorizationInfo();
	    String[] grants = jwtProv.getClaims(principals.toString()).get("grants", String[].class);
	    authorizationInfo.setRoles(new HashSet<>(Arrays.asList(grants)));
	} catch (Exception e) {
	    log.error("Error while retreiving username and grants.", e);
	    authorizationInfo = new SimpleAuthorizationInfo();
	}
	return authorizationInfo;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
	SimpleAuthenticationInfo authenticationInfo;
	try {
	    String username = jwtProv.getUser((String) token.getCredentials());
	    // TODO check if given username matches with some record in db.
	    authenticationInfo = new SimpleAuthenticationInfo(token, token, REALMNAME);
	} catch (Exception e) {
	    log.error("Error while checking username on persistence layer.", e);
	    authenticationInfo = new SimpleAuthenticationInfo();
	}
	return authenticationInfo;
    }
}
