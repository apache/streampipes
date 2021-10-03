package org.apache.streampipes.model.client.user;

public class JwtAuthenticationResponse {

	private String accessToken;

	public static JwtAuthenticationResponse from(String accessToken) {
		return new JwtAuthenticationResponse(accessToken);
	}

	private JwtAuthenticationResponse(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

}
