package de.fzi.cep.sepa.model.client.user;

public class ShiroAuthenticationResponse {

	private Info info;
	
	public ShiroAuthenticationResponse(Info info) {
		super();
		this.info = info;
	}

	public Info getInfo() {
		return info;
	}

	public void setInfo(Info info) {
		this.info = info;
	}
}
