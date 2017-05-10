package edu.pezzati.sec.model;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

@SuppressWarnings("serial")
public class Token implements Serializable {

    @JsonProperty("token_type")
    private static final String tokenType = "bearer";
    @JsonProperty("token")
    private String jwtToken;
    @JsonProperty("expires")
    private Date expires;

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

    public Date getExpires() {
	return expires;
    }

    public void setExpires(Date expires) {
	this.expires = expires;
    }

    @Override
    public boolean equals(Object obj) {
	if (obj == null)
	    return false;
	if (obj.getClass() != this.getClass())
	    return false;
	return this.jwtToken.equals(((Token) obj).jwtToken);
    }
}
