package org.streampipes.model.client.user;

import java.util.ArrayList;
import java.util.List;

public class ShiroAuthenticationResponseFactory {

	public static ShiroAuthenticationResponse create(User user)
	{
		Authc authc = new Authc(new Principal(user.getEmail(), ""), new Credentials(user.getEmail()));
		List<String> roles = new ArrayList<>();
		user.getRoles().forEach(r -> roles.add(r.toString()));
		Authz authz = new Authz(roles, new ArrayList<String>());
		
		Info info = new Info();
		info.setAuthc(authc);
		info.setAuthz(authz);
		ShiroAuthenticationResponse response = new ShiroAuthenticationResponse(info);
		
		return response;
	}
}
