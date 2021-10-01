package org.apache.streampipes.user.management.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.WeakKeyException;
import org.apache.streampipes.config.backend.BackendConfig;
import org.apache.streampipes.config.backend.model.LocalAuthConfig;
import org.apache.streampipes.user.management.model.LocalUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

public class JwtTokenProvider {

	private static final Logger LOG = LoggerFactory.getLogger(JwtTokenProvider.class);
	private BackendConfig config;

	public JwtTokenProvider() {
		this.config = BackendConfig.INSTANCE;
	}

	public String createToken(Authentication authentication) {
		LocalUser userPrincipal = (LocalUser) authentication.getPrincipal();
		Date tokenExpirationDate = makeExpirationDate();
		SecretKey key = Keys.hmacShaKeyFor(tokenSecret().getBytes(StandardCharsets.UTF_8));

		return Jwts
						.builder()
						.setSubject(userPrincipal.getEmail())
						.setIssuedAt(new Date())
						.setExpiration(tokenExpirationDate)
				.signWith(key).compact();
	}

	public String getUserIdFromToken(String token) {
		Claims claims = jwtParser().parseClaimsJws(token).getBody();
		return claims.getSubject();
	}

	public boolean validateJwtToken(String jwtToken) {
		try {
			jwtParser().parseClaimsJws(jwtToken);
			return true;
		} catch (MalformedJwtException ex) {
			LOG.error("Invalid JWT token");
		} catch (ExpiredJwtException ex) {
			LOG.error("Expired JWT token");
		} catch (UnsupportedJwtException ex) {
			LOG.error("Unsupported JWT token");
		} catch (IllegalArgumentException ex) {
			LOG.error("JWT claims are empty.");
		} catch (WeakKeyException ex) {
			LOG.error("Weak Key");
		}
		return false;
	}

	private JwtParser jwtParser() {
		return Jwts.parserBuilder()
						.setSigningKey(tokenSecret().getBytes(StandardCharsets.UTF_8))
						.build();
	}

	private String tokenSecret() {
		return authConfig().getTokenSecret();
	}

	private LocalAuthConfig authConfig() {
		return this.config.getLocalAuthConfig();
	}

	private Date makeExpirationDate() {
		Date now = new Date();
		return new Date(now.getTime() + authConfig().getTokenExpirationTimeMillis());
	}
}
