package org.apache.streampipes.user.management.jwt;

import org.apache.streampipes.config.backend.BackendConfig;
import org.apache.streampipes.config.backend.model.LocalAuthConfig;
import org.apache.streampipes.model.client.user.Principal;
import org.apache.streampipes.model.client.user.UserAccount;
import org.apache.streampipes.model.client.user.UserInfo;
import org.apache.streampipes.security.jwt.JwtTokenUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class JwtTokenProvider {

	public static final String CLAIM_USER = "user";

	private BackendConfig config;

	public JwtTokenProvider() {
		this.config = BackendConfig.INSTANCE;
	}

	public String createToken(Authentication authentication) {
		Principal userPrincipal = (Principal) authentication.getPrincipal();
		Set<String> roles = authentication
						.getAuthorities()
						.stream()
						.map(GrantedAuthority::getAuthority)
						.collect(Collectors.toSet());

		Date tokenExpirationDate = makeExpirationDate();
		Map<String, Object> claims = makeClaims(userPrincipal, roles);

		return JwtTokenUtils.makeJwtToken(userPrincipal.getPrincipalName(), tokenSecret(), claims, tokenExpirationDate);
	}

	private Map<String, Object> makeClaims(Principal principal,
																				 Set<String> roles) {
		Map<String, Object> claims = new HashMap<>();
		claims.put(CLAIM_USER, toUserInfo((UserAccount) principal, roles));

		return claims;
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

	private UserInfo toUserInfo(UserAccount localUser,
															Set<String> roles) {
		UserInfo userInfo = new UserInfo();
		userInfo.setUserId("id");
		userInfo.setEmail(localUser.getEmail());
		userInfo.setDisplayName(localUser.getUsername());
		userInfo.setShowTutorial(!localUser.isHideTutorial());
		userInfo.setRoles(roles);
		return userInfo;
	}
}
