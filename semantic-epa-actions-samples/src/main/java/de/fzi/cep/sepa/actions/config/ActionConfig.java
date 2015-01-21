package de.fzi.cep.sepa.actions.config;

public class ActionConfig {

	public final static String serverUrl;
	public final static String iconBaseUrl;
	
	static {
		serverUrl = "http://localhost:8091";
		iconBaseUrl = "http://localhost:8080/semantic-epa-backend/img";
	}
}
