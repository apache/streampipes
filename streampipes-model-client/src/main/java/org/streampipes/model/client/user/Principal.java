package org.streampipes.model.client.user;

public class Principal {

	private String username;
	private String email;
	private String apiKey;
	
	public Principal(String username, String email, String apiKey) {
		super();
		this.username = username;
		this.email = email;
		this.apiKey = apiKey;
	}
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getApiKey() {
		return apiKey;
	}
	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
}
