package de.fzi.cep.sepa.actions.config;

import de.fzi.cep.sepa.commons.config.Configuration;

public class ActionConfig {

	public final static String serverUrl;
	public final static String iconBaseUrl;
	
	static {
		serverUrl = Configuration.getInstance().ACTION_BASE_URL;
		iconBaseUrl = Configuration.getInstance().getHostname() +"8080" +"/semantic-epa-backend/img";
	}
}
