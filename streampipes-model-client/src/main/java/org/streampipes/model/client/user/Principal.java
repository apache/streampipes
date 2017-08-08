package org.streampipes.model.client.user;

public class Principal {

	private String email;
	private String apiKey;
	
	public Principal(String email, String apiKey) {
		super();
		this.email = email;
		this.apiKey = apiKey;
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
