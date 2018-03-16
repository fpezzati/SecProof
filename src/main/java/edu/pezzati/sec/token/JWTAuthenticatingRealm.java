package edu.pezzati.sec.token;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.realm.AuthenticatingRealm;

public class JWTAuthenticatingRealm extends AuthenticatingRealm {

    public JWTAuthenticatingRealm() {
	setAuthenticationTokenClass(JWTToken.class);
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
	// TODO Auto-generated method stub
	return null;
    }
}
