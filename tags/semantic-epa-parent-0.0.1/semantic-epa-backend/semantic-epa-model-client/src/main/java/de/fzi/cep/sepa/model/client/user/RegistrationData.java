package de.fzi.cep.sepa.model.client.user;

import java.util.List;

public class RegistrationData {

	private String username;
	private String password;
	private String email;
	private Role role;
	
	private List<String> roles;
	
	public RegistrationData()
	{
		
	}
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}

	public List<String> getRoles() {
		return roles;
	}

	public void setRoles(List<String> roles) {
		this.roles = roles;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role selectedRole) {
		this.role = selectedRole;
	}
	
	
}
