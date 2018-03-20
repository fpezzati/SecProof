package edu.pezzati.sec.token;

import org.apache.shiro.authc.AuthenticationToken;

public class JWTToken implements AuthenticationToken {

    private static final long serialVersionUID = -7611270013703900120L;
    private String principal;
    private String user;
    private String[] permissions;

    public JWTToken() {
    }

    public JWTToken(String principal, String user, String[] permissions) {
	this.principal = principal;
	this.user = user;
	this.setPermissions(permissions);
    }

    @Override
    public Object getPrincipal() {
	return principal;
    }

    public void setPrincipal(String principal) {
	this.principal = principal;
    }

    @Override
    public Object getCredentials() {
	return user;
    }

    public String[] getPermissions() {
	return permissions;
    }

    public void setPermissions(String[] permissions) {
	this.permissions = permissions;
    }
}
