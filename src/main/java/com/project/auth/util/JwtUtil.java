package com.project.auth.util;

import java.util.Base64;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {
	
	private static final SecretKey KEY = Jwts.SIG.HS512.key().build();
	private static final String SECRET = Base64.getEncoder().encodeToString(KEY.getEncoded());
	private static final long EXPIRATION_TIME = 86400000;
	
	private SecretKey getSigningKey() {		
		byte[] keyBytes = Decoders.BASE64.decode(SECRET);
		return Keys.hmacShaKeyFor(keyBytes);
	}
	
	public String generateToken(String username) {
		return Jwts.builder()
				.subject(username)
				.issuedAt(new Date())
				.expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
				.signWith(getSigningKey(), Jwts.SIG.HS512)
				.compact();
	}
	
	public String extractUsername(String token) {
		Claims data = Jwts.parser()
				.verifyWith(getSigningKey())
				.build().parseSignedClaims(token)
				.getPayload();
		
		String username = data.getSubject();
		
		if(data.getExpiration().before(new Date())) {
			return null;
		}
		
		return username;
	}
}
