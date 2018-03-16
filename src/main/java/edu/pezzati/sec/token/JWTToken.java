package edu.pezzati.sec.token;

import org.apache.shiro.authc.AuthenticationToken;

public class JWTToken implements AuthenticationToken {

    private static final long serialVersionUID = -7611270013703900120L;
    private String principal;

    @Override
    public Object getPrincipal() {
	return principal;
    }

    public void setPrincipal(String principal) {
	this.principal = principal;
    }

    @Override
    public Object getCredentials() {
	return null;
    }
}
