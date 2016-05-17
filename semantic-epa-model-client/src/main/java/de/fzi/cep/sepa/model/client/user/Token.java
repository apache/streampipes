package de.fzi.cep.sepa.model.client.user;

public class Token {

	private String principal;
	private String credentials;
	
	public Token(String principal, String credentials) {
		super();
		this.principal = principal;
		this.credentials = credentials;
	}

	public String getPrincipal() {
		return principal;
	}

	public void setPrincipal(String principal) {
		this.principal = principal;
	}

	public String getCredentials() {
		return credentials;
	}

	public void setCredentials(String credentials) {
		this.credentials = credentials;
	}
	
	
	
}
