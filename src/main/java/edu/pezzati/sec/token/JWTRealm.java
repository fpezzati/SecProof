package edu.pezzati.sec.token;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.Permission;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.authz.permission.WildcardPermission;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JWTRealm extends AuthorizingRealm {

    private Logger log = LoggerFactory.getLogger(getClass());
    private JwtTokenProvider jwtTokenProvider;

    public JWTRealm() {
	setAuthenticationTokenClass(JWTToken.class);
	jwtTokenProvider = new JwtTokenProvider();
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
	log.info("Principals: {}", principals.toString());
	try {
	    String[] permissions = jwtTokenProvider.getPermissions(principals.toString());
	    SimpleAuthorizationInfo authInfo = new SimpleAuthorizationInfo();
	    authInfo.addObjectPermissions(buildPermissions(permissions));
	    return authInfo;
	} catch (Exception e) {
	    log.error("Error on retreiving user permissions from token", e);
	    return null;
	}
    }

    private Collection<Permission> buildPermissions(String[] permissions) {
	Set<Permission> perms = new HashSet<>();
	for (String permission : permissions) {
	    perms.add(new WildcardPermission(permission));
	}
	return perms;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
	log.info("Token: {}", token);
	AuthenticationInfo sAuthInfo = new SimpleAuthenticationInfo(token.getPrincipal(), token.getCredentials(), getName());
	return sAuthInfo;
    }
}
