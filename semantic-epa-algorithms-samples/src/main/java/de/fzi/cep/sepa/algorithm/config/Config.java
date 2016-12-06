package de.fzi.cep.sepa.algorithm.config;

import de.fzi.cep.sepa.commons.config.ClientConfiguration;

public class Config {

	public final static String serverUrl;
	public final static String iconBaseUrl;
	
	static {
		serverUrl = ClientConfiguration.INSTANCE.getAlgorithmUrl();
		iconBaseUrl = ClientConfiguration.INSTANCE.getWebappUrl() +"/semantic-builder-backend/img";
	}
	
}
