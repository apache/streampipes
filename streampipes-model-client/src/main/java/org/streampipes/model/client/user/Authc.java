package org.streampipes.model.client.user;

public class Authc {

	private Principal principal;
	private Credentials credentials;
	
	public Authc(Principal principal, Credentials credentials) {
		super();
		this.principal = principal;
		this.credentials = credentials;
	}
	
	public Principal getPrincipal() {
		return principal;
	}
	public void setPrincipal(Principal principal) {
		this.principal = principal;
	}
	public Credentials getCredentials() {
		return credentials;
	}
	public void setCredentials(Credentials credentials) {
		this.credentials = credentials;
	}
}
