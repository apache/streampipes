package de.fzi.cep.sepa.component.config;

import de.fzi.cep.sepa.commons.config.Configuration;

public class Config {

	public final static String serverUrl;
	public final static String iconBaseUrl;
	
	static {
		serverUrl = Configuration.getInstance().ALGORITHM_BASE_URL;
		iconBaseUrl = Configuration.getInstance().WEBAPP_BASE_URL +"/semantic-builder-backend/img";
	}
	
}
