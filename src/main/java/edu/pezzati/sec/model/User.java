package edu.pezzati.sec.model;

public class User {

    private String username;
    private String[] permissions;

    public String getUsername() {
	return username;
    }

    public void setUsername(String username) {
	this.username = username;
    }

    public String[] getPermissions() {
	return permissions;
    }

    public void setPermissions(String[] permissions) {
	this.permissions = permissions;
    }
}
