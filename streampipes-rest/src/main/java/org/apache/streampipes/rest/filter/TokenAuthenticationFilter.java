package org.apache.streampipes.rest.filter;

import org.apache.streampipes.storage.api.IUserStorage;
import org.apache.streampipes.storage.management.StorageDispatcher;
import org.apache.streampipes.user.management.jwt.JwtTokenProvider;
import org.apache.streampipes.user.management.model.LocalUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class TokenAuthenticationFilter extends OncePerRequestFilter {


	private JwtTokenProvider tokenProvider;

	private IUserStorage userStorage;

	private static final Logger logger = LoggerFactory.getLogger(TokenAuthenticationFilter.class);

	public TokenAuthenticationFilter() {
		this.tokenProvider = new JwtTokenProvider();
		this.userStorage = StorageDispatcher.INSTANCE.getNoSqlStore().getUserStorageAPI();
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request,
																	HttpServletResponse response,
																	FilterChain filterChain) throws ServletException, IOException {
		try {
			String jwt = getJwtFromRequest(request);

			if (StringUtils.hasText(jwt) && tokenProvider.validateJwtToken(jwt)) {
				String username = tokenProvider.getUserIdFromToken(jwt);

				UserDetails userDetails = new LocalUser(userStorage.getUser(username));
				UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
				authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

				SecurityContextHolder.getContext().setAuthentication(authentication);
			}
		} catch (Exception ex) {
			logger.error("Could not set user authentication in security context", ex);
		}

		filterChain.doFilter(request, response);

	}

	private String getJwtFromRequest(HttpServletRequest request) {
		String bearerToken = request.getHeader("Authorization");
		if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
			return bearerToken.substring(7);
		}
		return null;
	}
}
