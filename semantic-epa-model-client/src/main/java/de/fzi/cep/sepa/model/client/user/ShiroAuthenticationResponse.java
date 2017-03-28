package de.fzi.cep.sepa.model.client.user;

public class ShiroAuthenticationResponse {

	private Info info;
	private boolean success;
	private String callbackUrl;
	private String token;
	
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

	public String getCallbackUrl() {
		return callbackUrl;
	}

	public void setCallbackUrl(String callbackUrl) {
		this.callbackUrl = callbackUrl;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
}
