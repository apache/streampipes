package de.fzi.cep.sepa.model.client.user;

public class ShiroAuthenticationResponse {

	private Info info;
	private boolean success;
	
	public ShiroAuthenticationResponse(Info info) {
		super();
		this.info = info;
		this.success = true;
	}

	public Info getInfo() {
		return info;
	}

	public void setInfo(Info info) {
		this.info = info;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}
	
}
