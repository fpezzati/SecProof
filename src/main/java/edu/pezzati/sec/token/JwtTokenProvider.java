package edu.pezzati.sec.token;

import java.util.Base64;
import java.util.Date;

import edu.pezzati.sec.model.Token;
import edu.pezzati.sec.model.User;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;

public class JwtTokenProvider {

    private final long tokenLifetime = Long.parseLong(System.getProperty("jwt.token.lifetime"));
    private final String jwtSecret = new String(Base64.getEncoder().encode(System.getProperty("jwt.shared.secret").getBytes()));

    public Token getJwtToken(User user) {
	Date exp = new Date(System.currentTimeMillis() + tokenLifetime * 1000);
	return new Token(
		Jwts.builder().setSubject(user.getUsername()).signWith(SignatureAlgorithm.HS512, jwtSecret).setExpiration(exp).compact());
    }

    public User verifyToken(String token) throws SignatureException, JwtException, Exception {
	String username = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
	User user = new User();
	user.setUsername(username);
	return user;
    }
}
