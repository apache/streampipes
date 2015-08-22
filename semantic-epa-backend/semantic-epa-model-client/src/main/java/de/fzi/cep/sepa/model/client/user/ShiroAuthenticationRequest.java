package de.fzi.cep.sepa.model.client.user;

public class ShiroAuthenticationRequest {

	private String username;
	private String password;
	
	public ShiroAuthenticationRequest(String username, String password) {
		super();
		this.username = username;
		this.password = password;
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

	
}
