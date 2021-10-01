package org.apache.streampipes.model.client.user;

public class JwtAuthenticationResponse {

	private String accessToken;
	private UserInfo userInfo;

	public static JwtAuthenticationResponse from(String accessToken,
																							 UserInfo userInfo) {
		return new JwtAuthenticationResponse(accessToken, userInfo);
	}

	private JwtAuthenticationResponse(String accessToken,
																		UserInfo userInfo) {
		this.accessToken = accessToken;
		this.userInfo = userInfo;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public UserInfo getUserInfo() {
		return userInfo;
	}

	public void setUserInfo(UserInfo userInfo) {
		this.userInfo = userInfo;
	}
}
