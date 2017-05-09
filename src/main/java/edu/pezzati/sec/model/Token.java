package edu.pezzati.sec.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

@SuppressWarnings("serial")
public class Token implements Serializable {

    @JsonProperty("token_type")
    private static final String tokenType = "bearer";
    @JsonProperty("token")
    private String jwtToken;

    public Token() {

    }

    public Token(String jwtToken) {
	this.jwtToken = jwtToken;
    }

    public String getJwtToken() {
	return jwtToken;
    }

    public void setJwtToken(String jwtToken) {
	this.jwtToken = jwtToken;
    }
}
