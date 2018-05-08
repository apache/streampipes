/*
 * Copyright 2018 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

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
