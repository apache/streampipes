package org.streampipes.model.client.user;

import java.util.List;

public class Authz {

	private List<String> roles;
	private List<String> permissions;
	
	public Authz(List<String> roles, List<String> permissions) {
		super();
		this.roles = roles;
		this.permissions = permissions;
	}
	
	public List<String> getRoles() {
		return roles;
	}
	public void setRoles(List<String> roles) {
		this.roles = roles;
	}
	public List<String> getPermissions() {
		return permissions;
	}
	public void setPermissions(List<String> permissions) {
		this.permissions = permissions;
	}
	
}
