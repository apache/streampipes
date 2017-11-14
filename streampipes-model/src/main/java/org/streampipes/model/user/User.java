package org.streampipes.model.user;

import java.util.List;

public class User {

	private String firstName;
	private String lastName;
	
	private String email;
	
	private List<String> roles;
	
	private String slackUserName;

	
	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
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

	public String getSlackUserName() {
		return slackUserName;
	}

	public void setSlackUserName(String slackUserName) {
		this.slackUserName = slackUserName;
	}
	
	
	
}
