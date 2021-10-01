package org.apache.streampipes.user.management.jwt;

import org.apache.streampipes.config.backend.BackendConfig;
import org.apache.streampipes.config.backend.model.LocalAuthConfig;
import org.apache.streampipes.model.client.user.Principal;
import org.apache.streampipes.security.jwt.JwtTokenUtils;
import org.springframework.security.core.Authentication;

import java.util.Date;

public class JwtTokenProvider {

	private BackendConfig config;

	public JwtTokenProvider() {
		this.config = BackendConfig.INSTANCE;
	}

	public String createToken(Authentication authentication) {
		Principal userPrincipal = (Principal) authentication.getPrincipal();
		Date tokenExpirationDate = makeExpirationDate();

		return JwtTokenUtils.makeJwtToken(userPrincipal.getPrincipalName(), tokenSecret(), tokenExpirationDate);
	}

	public String getUserIdFromToken(String token) {
		return JwtTokenUtils.getUserIdFromToken(token, new SpKeyResolver(tokenSecret()));
	}

	public boolean validateJwtToken(String jwtToken) {
		return JwtTokenUtils.validateJwtToken(jwtToken, new SpKeyResolver(tokenSecret()));
	}

	public boolean validateJwtToken(String tokenSecret,
																	String jwtToken) {
		return JwtTokenUtils.validateJwtToken(tokenSecret, jwtToken);
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
