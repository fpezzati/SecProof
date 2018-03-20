package edu.pezzati.sec.token;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;

import edu.pezzati.sec.model.Token;
import edu.pezzati.sec.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;

public class JwtTokenProvider {

    private final long tokenLifetime = Long.parseLong(System.getProperty("jwt.token.lifetime"));
    private final String jwtSecret = new String(Base64.getEncoder().encode(System.getProperty("jwt.shared.secret").getBytes()));
    private JwtParser jwtParser;

    public JwtTokenProvider() {
	jwtParser = Jwts.parser().setSigningKey(jwtSecret);
    }

    public Token getJwtToken(User user) {
	Date exp = new Date(System.currentTimeMillis() + tokenLifetime * 1000);
	return new Token(
		Jwts.builder().setSubject(user.getUsername()).signWith(SignatureAlgorithm.HS512, jwtSecret).setExpiration(exp).compact());
    }

    public String getUser(String token) throws SignatureException, JwtException, Exception {
	return jwtParser.parseClaimsJws(token).getBody().getSubject();
    }

    public Claims getClaims(String token) throws SignatureException, JwtException, Exception {
	return jwtParser.parseClaimsJws(token).getBody();
    }

    public Token refreshToken(Token expiredToken) throws Exception {
	return null;
    }

    public String[] getPermissions(String token) throws Exception {
	Claims claims = getClaims(token);
	@SuppressWarnings("unchecked")
	List<String> permissions = claims.get("permissions", ArrayList.class);
	return permissions.toArray(new String[permissions.size()]);
    }
}
