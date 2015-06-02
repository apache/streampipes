package de.fzi.cep.sepa.actions.config;

import de.fzi.cep.sepa.commons.config.Configuration;

public class ActionConfig {

	public final static String serverUrl;
	public final static String iconBaseUrl;
	
	static {
		serverUrl = Configuration.ACTION_BASE_URL;
		iconBaseUrl = Configuration.WEBAPP_BASE_URL +"/semantic-epa-backend/img";
	}
}
